<script setup>
import { computed, reactive, ref, watch } from 'vue'
import StatusContrato from '@/components/StatusContrato.vue'
import {
  concederCredito,
  leasingsPendentes,
  locacoesAtivas,
  meusContratos,
  registrarDevolucao,
} from '@/services/contratoApi'
import { rotuloModalidade } from '@/services/pedidoApi'
import { authStore } from '@/store/auth'

const contratos = ref([])
const carregando = ref(false)
const erro = ref('')
const processando = ref(null)
const inputs = reactive({})

const papel = computed(() => authStore.usuario?.tipo)

function inputDe(id) {
  if (!inputs[id]) {
    inputs[id] = { data: '', valor: '' }
  }
  return inputs[id]
}

async function carregar() {
  carregando.value = true
  erro.value = ''
  try {
    if (papel.value === 'CLIENTE') {
      contratos.value = await meusContratos()
    } else if (papel.value === 'EMPRESA') {
      contratos.value = await locacoesAtivas()
    } else if (papel.value === 'BANCO') {
      contratos.value = await leasingsPendentes()
    }
  } catch (e) {
    erro.value = e.message
  } finally {
    carregando.value = false
  }
}

watch(
  () => authStore.usuario,
  (usuario) => {
    if (usuario) {
      carregar()
    }
  },
  { immediate: true },
)

async function devolver(contrato) {
  processando.value = contrato.id
  erro.value = ''
  try {
    await registrarDevolucao(contrato.id, inputDe(contrato.id).data)
    await carregar()
  } catch (e) {
    erro.value = e.message
  } finally {
    processando.value = null
  }
}

async function conceder(contrato) {
  processando.value = contrato.id
  erro.value = ''
  try {
    await concederCredito(contrato.id, Number(inputDe(contrato.id).valor))
    await carregar()
  } catch (e) {
    erro.value = e.message
  } finally {
    processando.value = null
  }
}

function formatarDataHora(iso) {
  return iso ? new Date(iso).toLocaleString('pt-BR', { dateStyle: 'short', timeStyle: 'short' }) : '—'
}
</script>

<template>
  <main class="contratos">
    <template v-if="!authStore.usuario">
      <h1>Contratos</h1>
      <p class="nota">Você precisa <RouterLink to="/login">entrar</RouterLink> para ver esta página.</p>
    </template>

    <template v-else>
      <h1 v-if="papel === 'CLIENTE'">Meus contratos</h1>
      <h1 v-else-if="papel === 'EMPRESA'">Locações ativas</h1>
      <h1 v-else-if="papel === 'BANCO'">Leasings pendentes de crédito</h1>

      <template v-if="['CLIENTE', 'EMPRESA', 'BANCO'].includes(papel)">
        <p v-if="erro" class="aviso aviso-erro">{{ erro }}</p>
        <p v-if="carregando">Carregando...</p>
        <template v-else-if="!erro">
          <p v-if="contratos.length === 0" class="nota">Nenhum contrato encontrado.</p>

          <ul v-else class="lista">
            <li v-for="c in contratos" :key="c.id" class="contrato">
              <div class="cabecalho-contrato">
                <span class="numero">Contrato #{{ c.id }} · {{ rotuloModalidade(c.tipo) }}</span>
                <StatusContrato :situacao="c.situacao" />
              </div>
              <p class="automovel">
                {{ c.automovel.marca }} {{ c.automovel.modelo }} {{ c.automovel.ano }}
                <span class="placa">{{ c.automovel.placa }}</span>
              </p>
              <dl class="dados">
                <div v-if="papel !== 'CLIENTE'">
                  <dt>Cliente</dt>
                  <dd>{{ c.nomeCliente }}</dd>
                </div>
                <div>
                  <dt>Início</dt>
                  <dd>{{ formatarDataHora(c.dataInicio) }}</dd>
                </div>
                <template v-if="c.tipo === 'LOCACAO'">
                  <div>
                    <dt>Prazo</dt>
                    <dd>{{ c.prazo }} dias</dd>
                  </div>
                  <div>
                    <dt>Devolução prevista</dt>
                    <dd>{{ c.dataPrevistaDevolucao }}</dd>
                  </div>
                  <div v-if="c.dataEfetivaDevolucao">
                    <dt>Devolução efetiva</dt>
                    <dd>{{ c.dataEfetivaDevolucao }}</dd>
                  </div>
                </template>
                <template v-if="c.tipo === 'ASSINATURA'">
                  <div>
                    <dt>Mensalidade</dt>
                    <dd>{{ c.valorMensalidade }} ({{ c.recorrencia }})</dd>
                  </div>
                </template>
                <template v-if="c.tipo === 'LEASING' && c.contratoCredito">
                  <div>
                    <dt>Crédito concedido</dt>
                    <dd>{{ c.contratoCredito.valor }} por {{ c.contratoCredito.banco }}</dd>
                  </div>
                </template>
              </dl>

              <div v-if="papel === 'EMPRESA' && c.situacao === 'EM_EXECUCAO'" class="acao">
                <label>
                  Data de devolução
                  <input type="date" v-model="inputDe(c.id).data" />
                </label>
                <button type="button" :disabled="processando === c.id || !inputDe(c.id).data" @click="devolver(c)">
                  Registrar devolução
                </button>
              </div>

              <div v-if="papel === 'BANCO' && c.situacao === 'PENDENTE'" class="acao">
                <label>
                  Valor do crédito
                  <input type="number" step="0.01" min="0" v-model="inputDe(c.id).valor" />
                </label>
                <button type="button" :disabled="processando === c.id || !inputDe(c.id).valor" @click="conceder(c)">
                  Conceder crédito
                </button>
              </div>
            </li>
          </ul>
        </template>
      </template>
      <p v-else class="nota">Esta conta ({{ authStore.usuario.tipo }}) não tem acesso a contratos.</p>
    </template>
  </main>
</template>

<style scoped>
.contratos {
  max-width: 720px;
  margin: 0 auto;
  padding: 2rem 1rem;
}

.nota {
  font-size: 0.9rem;
}

.lista {
  list-style: none;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  margin-top: 1rem;
}

.contrato {
  border: 1px solid var(--color-border);
  border-radius: 8px;
  padding: 0.9rem 1.1rem;
}

.cabecalho-contrato {
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

.acao {
  margin-top: 0.75rem;
  padding-top: 0.75rem;
  border-top: 1px solid var(--color-border);
  display: flex;
  align-items: flex-end;
  gap: 0.75rem;
  flex-wrap: wrap;
}

.acao label {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
  font-size: 0.85rem;
}

.acao input {
  padding: 0.4rem 0.55rem;
  border: 1px solid var(--color-border);
  border-radius: 4px;
  background: var(--color-background);
  color: var(--color-text);
}

.acao button {
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 4px;
  background: hsla(160, 100%, 37%, 1);
  color: #fff;
  cursor: pointer;
}

.acao button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.aviso {
  padding: 0.6rem 1rem;
  border-radius: 4px;
  margin-bottom: 1rem;
}

.aviso-erro {
  background: hsla(0, 90%, 50%, 0.12);
  color: hsl(0, 80%, 45%);
}
</style>
