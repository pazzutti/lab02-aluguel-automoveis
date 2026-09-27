<script setup>
import { computed, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import StatusPedido from '@/components/StatusPedido.vue'
import { SITUACOES, meusPedidos, pedidosPendentes, rotuloModalidade } from '@/services/pedidoApi'
import { authStore } from '@/store/auth'

const route = useRoute()
const pedidos = ref([])
const carregando = ref(false)
const erro = ref('')

// Mesma tela para os dois perfis (RF25): o cliente ve os proprios pedidos
// (HU07) e o agente (empresa/banco) ve a fila de pendentes (HU10).
const ehCliente = computed(() => authStore.usuario?.tipo === 'CLIENTE')
const ehAgente = computed(() => ['EMPRESA', 'BANCO'].includes(authStore.usuario?.tipo))
const pedidoCriado = computed(() => route.query.criado)

async function carregarPedidos() {
  carregando.value = true
  erro.value = ''
  try {
    pedidos.value = ehCliente.value ? await meusPedidos() : await pedidosPendentes()
  } catch (e) {
    erro.value = e.message
  } finally {
    carregando.value = false
  }
}

watch(
  () => authStore.usuario,
  (usuario) => {
    if (usuario && (ehCliente.value || ehAgente.value)) {
      carregarPedidos()
    }
  },
  { immediate: true },
)

function formatarData(iso) {
  return iso ? new Date(iso).toLocaleString('pt-BR', { dateStyle: 'short', timeStyle: 'short' }) : '—'
}
</script>

<template>
  <main class="pedidos">
    <template v-if="!authStore.usuario">
      <h1>Pedidos</h1>
      <p class="nota">Você precisa <RouterLink to="/login">entrar</RouterLink> para ver os pedidos.</p>
    </template>

    <template v-else-if="ehCliente || ehAgente">
      <div class="topo">
        <h1>{{ ehCliente ? 'Meus pedidos' : 'Pedidos pendentes de análise' }}</h1>
        <RouterLink v-if="ehCliente" class="botao" to="/pedidos/novo">Novo pedido</RouterLink>
      </div>

      <p v-if="pedidoCriado" class="aviso aviso-sucesso">
        Pedido #{{ pedidoCriado }} criado. Ele ficará como "não avaliado" até um agente iniciar a análise.
      </p>
      <p v-if="erro" class="aviso aviso-erro">{{ erro }}</p>

      <p v-if="carregando">Carregando...</p>
      <template v-else-if="!erro">
        <p v-if="pedidos.length === 0" class="nota">
          <template v-if="ehCliente">
            Você ainda não fez nenhum pedido. <RouterLink to="/pedidos/novo">Fazer o primeiro</RouterLink>.
          </template>
          <template v-else>Nenhum pedido aguardando análise.</template>
        </p>

        <ul v-else class="lista">
          <li v-for="p in pedidos" :key="p.id" class="pedido" :class="{ destaque: String(p.id) === pedidoCriado }">
            <div class="cabecalho-pedido">
              <span class="numero">Pedido #{{ p.id }}</span>
              <StatusPedido :situacao="p.situacao" />
            </div>
            <p class="automovel">
              {{ p.automovel.marca }} {{ p.automovel.modelo }} {{ p.automovel.ano }}
              <span class="placa">{{ p.automovel.placa }}</span>
            </p>
            <dl class="dados">
              <div v-if="ehAgente">
                <dt>Cliente</dt>
                <dd>{{ p.nomeCliente }}</dd>
              </div>
              <div>
                <dt>Modalidade</dt>
                <dd>{{ rotuloModalidade(p.modalidade) }}</dd>
              </div>
              <div>
                <dt>Criado em</dt>
                <dd>{{ formatarData(p.dataCriacao) }}</dd>
              </div>
              <div v-if="p.dataAlteracao">
                <dt>Alterado em</dt>
                <dd>{{ formatarData(p.dataAlteracao) }}</dd>
              </div>
            </dl>
            <p v-if="ehCliente" class="explicacao">{{ SITUACOES[p.situacao]?.descricao }}</p>
          </li>
        </ul>
      </template>
    </template>

    <template v-else>
      <h1>Pedidos</h1>
      <p class="nota">Esta conta ({{ authStore.usuario.tipo }}) não tem acesso a pedidos.</p>
    </template>
  </main>
</template>

<style scoped>
.pedidos {
  max-width: 720px;
  margin: 0 auto;
  padding: 2rem 1rem;
}

.topo {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 0.75rem;
  margin-bottom: 1rem;
}

.nota {
  font-size: 0.9rem;
}

.botao {
  display: inline-block;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  text-decoration: none;
  background: hsla(160, 100%, 37%, 1);
  color: #fff;
}

.lista {
  list-style: none;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

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

.aviso {
  padding: 0.6rem 1rem;
  border-radius: 4px;
  margin-bottom: 1rem;
}

.aviso-sucesso {
  background: hsla(160, 100%, 37%, 0.15);
  color: hsl(160, 100%, 25%);
}

.aviso-erro {
  background: hsla(0, 90%, 50%, 0.12);
  color: hsl(0, 80%, 45%);
}
</style>
