<script setup>
import { reactive, ref } from 'vue'
import StatusPedido from '@/components/StatusPedido.vue'
import {
  SITUACOES,
  RESULTADOS,
  MODALIDADES,
  rotuloModalidade,
  cancelarPedido,
  alterarPedido,
  iniciarAnalise,
  registrarParecer,
  decidirPedido,
} from '@/services/pedidoApi'
import { listarAutomoveisDisponiveis } from '@/services/automovelApi'

const props = defineProps({
  pedido: { type: Object, required: true },
  papel: { type: String, required: true }, // 'CLIENTE' | 'AGENTE'
  destaque: { type: Boolean, default: false },
})
const emit = defineEmits(['atualizado'])

const processando = ref(false)
const erro = ref('')

const decisao = reactive({
  aceitar: true,
  prazo: '',
  dataPrevistaDevolucao: '',
  valorMensalidade: '',
  recorrencia: '',
})
const parecer = reactive({ resultado: 'FAVORAVEL', justificativa: '' })

const editando = ref(false)
const carregandoAutomoveis = ref(false)
const automoveisDisponiveis = ref([])
const edicao = reactive({ automovelId: null, modalidade: 'LOCACAO' })

function formatarData(iso) {
  return iso ? new Date(iso).toLocaleString('pt-BR', { dateStyle: 'short', timeStyle: 'short' }) : '—'
}

async function executar(acao) {
  processando.value = true
  erro.value = ''
  try {
    await acao()
    emit('atualizado')
  } catch (e) {
    erro.value = e.message
  } finally {
    processando.value = false
  }
}

function cancelar() {
  executar(() => cancelarPedido(props.pedido.id))
}

function iniciar() {
  executar(() => iniciarAnalise(props.pedido.id))
}

function enviarParecer() {
  executar(() => registrarParecer(props.pedido.id, { ...parecer }))
}

function enviarDecisao() {
  const payload = { aceitar: decisao.aceitar }
  if (decisao.aceitar) {
    if (props.pedido.modalidade === 'LOCACAO') {
      payload.prazo = Number(decisao.prazo)
      payload.dataPrevistaDevolucao = decisao.dataPrevistaDevolucao
    } else if (props.pedido.modalidade === 'ASSINATURA') {
      payload.valorMensalidade = Number(decisao.valorMensalidade)
      payload.recorrencia = decisao.recorrencia
    }
  }
  executar(() => decidirPedido(props.pedido.id, payload))
}

async function abrirEdicao() {
  edicao.automovelId = props.pedido.automovel.id
  edicao.modalidade = props.pedido.modalidade
  editando.value = true
  erro.value = ''
  carregandoAutomoveis.value = true
  try {
    automoveisDisponiveis.value = await listarAutomoveisDisponiveis()
  } catch (e) {
    erro.value = e.message
  } finally {
    carregandoAutomoveis.value = false
  }
}

function fecharEdicao() {
  editando.value = false
}

async function enviarEdicao() {
  processando.value = true
  erro.value = ''
  try {
    await alterarPedido(props.pedido.id, { automovelId: edicao.automovelId, modalidade: edicao.modalidade })
    editando.value = false
    emit('atualizado')
  } catch (e) {
    erro.value = e.message
  } finally {
    processando.value = false
  }
}
</script>

<template>
  <li class="pedido" :class="{ destaque }">
    <div class="cabecalho-pedido">
      <span class="numero">Pedido #{{ pedido.id }}</span>
      <StatusPedido :situacao="pedido.situacao" />
    </div>
    <p class="automovel">
      {{ pedido.automovel.marca }} {{ pedido.automovel.modelo }} {{ pedido.automovel.ano }}
      <span class="placa">{{ pedido.automovel.placa }}</span>
    </p>
    <dl class="dados">
      <div v-if="papel === 'AGENTE'">
        <dt>Cliente</dt>
        <dd>{{ pedido.nomeCliente }}</dd>
      </div>
      <div>
        <dt>Modalidade</dt>
        <dd>{{ rotuloModalidade(pedido.modalidade) }}</dd>
      </div>
      <div>
        <dt>Criado em</dt>
        <dd>{{ formatarData(pedido.dataCriacao) }}</dd>
      </div>
      <div v-if="pedido.dataAlteracao">
        <dt>Alterado em</dt>
        <dd>{{ formatarData(pedido.dataAlteracao) }}</dd>
      </div>
    </dl>

    <p v-if="papel === 'CLIENTE'" class="explicacao">{{ SITUACOES[pedido.situacao]?.descricao }}</p>

    <div v-if="pedido.parecer" class="parecer">
      <strong>Parecer do agente ({{ RESULTADOS[pedido.parecer.resultado]?.rotulo }}):</strong>
      {{ pedido.parecer.justificativa }}
    </div>

    <p v-if="erro" class="aviso aviso-erro">{{ erro }}</p>

    <div
      v-if="papel === 'CLIENTE' && !editando && ['NAO_AVALIADO', 'EM_ANALISE'].includes(pedido.situacao)"
      class="acoes-pedido"
    >
      <button type="button" class="secundario" :disabled="processando" @click="abrirEdicao">Editar pedido</button>
      <button type="button" class="secundario" :disabled="processando" @click="cancelar">Cancelar pedido</button>
    </div>

    <form
      v-if="papel === 'CLIENTE' && editando"
      class="form-inline"
      @submit.prevent="enviarEdicao"
    >
      <p v-if="carregandoAutomoveis">Carregando automóveis...</p>
      <template v-else>
        <div class="automoveis-edicao">
          <label
            v-for="a in automoveisDisponiveis"
            :key="a.id"
            class="opcao-automovel"
            :class="{ selecionada: edicao.automovelId === a.id }"
          >
            <input type="radio" v-model="edicao.automovelId" :value="a.id" :name="`automovel-${pedido.id}`" />
            <span class="modelo">{{ a.marca }} {{ a.modelo }}</span>
            <span class="detalhe">{{ a.ano }} · placa {{ a.placa }}</span>
          </label>
        </div>
        <div class="opcoes">
          <label v-for="m in MODALIDADES" :key="m.valor">
            <input type="radio" :value="m.valor" v-model="edicao.modalidade" /> {{ m.rotulo }}
          </label>
        </div>
      </template>
      <div class="acoes-formulario">
        <button type="submit" :disabled="processando || !edicao.automovelId">Salvar alterações</button>
        <button type="button" class="secundario" :disabled="processando" @click="fecharEdicao">Cancelar edição</button>
      </div>
    </form>

    <form
      v-if="papel === 'CLIENTE' && pedido.situacao === 'AVALIADO'"
      class="form-inline"
      @submit.prevent="enviarDecisao"
    >
      <div class="opcoes">
        <label><input type="radio" :value="true" v-model="decisao.aceitar" /> Aceitar</label>
        <label><input type="radio" :value="false" v-model="decisao.aceitar" /> Recusar</label>
      </div>
      <template v-if="decisao.aceitar && pedido.modalidade === 'LOCACAO'">
        <label>
          Prazo (dias)
          <input type="number" min="1" v-model="decisao.prazo" required />
        </label>
        <label>
          Data prevista de devolução
          <input type="date" v-model="decisao.dataPrevistaDevolucao" required />
        </label>
      </template>
      <template v-if="decisao.aceitar && pedido.modalidade === 'ASSINATURA'">
        <label>
          Valor da mensalidade
          <input type="number" step="0.01" min="0" v-model="decisao.valorMensalidade" required />
        </label>
        <label>
          Recorrência
          <input v-model="decisao.recorrencia" placeholder="Mensal" required />
        </label>
      </template>
      <button type="submit" :disabled="processando">Confirmar decisão</button>
    </form>

    <div v-if="papel === 'AGENTE' && pedido.situacao === 'NAO_AVALIADO'" class="acoes-pedido">
      <button type="button" :disabled="processando" @click="iniciar">Iniciar análise</button>
    </div>

    <form
      v-if="papel === 'AGENTE' && pedido.situacao === 'EM_ANALISE'"
      class="form-inline"
      @submit.prevent="enviarParecer"
    >
      <div class="opcoes">
        <label><input type="radio" value="FAVORAVEL" v-model="parecer.resultado" /> Favorável</label>
        <label><input type="radio" value="DESFAVORAVEL" v-model="parecer.resultado" /> Desfavorável</label>
      </div>
      <label class="campo-largo">
        Justificativa
        <textarea v-model="parecer.justificativa" required></textarea>
      </label>
      <button type="submit" :disabled="processando">Registrar parecer</button>
    </form>
  </li>
</template>

<style scoped>
.pedido {
  border: 1px solid var(--color-border);
  border-radius: 8px;
  padding: 0.9rem 1.1rem;
}

.pedido.destaque {
  border-color: hsla(160, 100%, 37%, 1);
}

.cabecalho-pedido {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 0.5rem;
}

.numero {
  font-size: 0.85rem;
  opacity: 0.75;
}

.automovel {
  margin: 0.35rem 0 0.6rem;
  font-weight: bold;
  color: var(--color-heading);
}

.placa {
  margin-left: 0.4rem;
  padding: 0.05rem 0.4rem;
  border: 1px solid var(--color-border);
  border-radius: 3px;
  font-family: monospace;
  font-weight: normal;
  font-size: 0.85rem;
}

.dados {
  display: flex;
  flex-wrap: wrap;
  gap: 0.4rem 1.5rem;
  font-size: 0.85rem;
}

.dados dt {
  opacity: 0.7;
}

.dados dd {
  margin: 0;
}

.explicacao {
  margin-top: 0.6rem;
  font-size: 0.8rem;
  opacity: 0.75;
}

.parecer {
  margin-top: 0.6rem;
  padding: 0.5rem 0.7rem;
  border-radius: 4px;
  background: var(--color-background-mute);
  font-size: 0.85rem;
}

.acoes-pedido {
  margin-top: 0.75rem;
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.acoes-formulario {
  display: flex;
  gap: 0.5rem;
}

.automoveis-edicao {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
  gap: 0.5rem;
}

.opcao-automovel {
  display: flex;
  flex-direction: column;
  border: 1px solid var(--color-border);
  border-radius: 6px;
  padding: 0.5rem 0.7rem;
  cursor: pointer;
  font-size: 0.85rem;
}

.opcao-automovel input {
  display: none;
}

.opcao-automovel .modelo {
  font-weight: bold;
  color: var(--color-heading);
}

.opcao-automovel .detalhe {
  font-size: 0.8rem;
  opacity: 0.8;
}

.opcao-automovel.selecionada {
  border-color: hsla(160, 100%, 37%, 1);
  background: hsla(160, 100%, 37%, 0.12);
}

.form-inline {
  margin-top: 0.75rem;
  padding-top: 0.75rem;
  border-top: 1px solid var(--color-border);
  display: flex;
  flex-direction: column;
  gap: 0.6rem;
}

.opcoes {
  display: flex;
  gap: 1rem;
  font-size: 0.9rem;
}

.form-inline label {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
  font-size: 0.85rem;
}

.campo-largo {
  width: 100%;
}

.form-inline input,
.form-inline textarea {
  padding: 0.4rem 0.55rem;
  border: 1px solid var(--color-border);
  border-radius: 4px;
  background: var(--color-background);
  color: var(--color-text);
  font: inherit;
}

.form-inline textarea {
  min-height: 4rem;
  resize: vertical;
}

.form-inline button,
.acoes-pedido button {
  align-self: flex-start;
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 4px;
  background: hsla(160, 100%, 37%, 1);
  color: #fff;
  cursor: pointer;
}

.acoes-pedido button.secundario {
  background: none;
  border: 1px solid var(--color-border);
  color: var(--color-text);
}

.form-inline button:disabled,
.acoes-pedido button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.aviso {
  padding: 0.5rem 0.8rem;
  border-radius: 4px;
  margin-top: 0.6rem;
  font-size: 0.85rem;
}

.aviso-erro {
  background: hsla(0, 90%, 50%, 0.12);
  color: hsl(0, 80%, 45%);
}
</style>
