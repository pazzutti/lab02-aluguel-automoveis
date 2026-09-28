<script setup>
import { computed, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import PedidoCard from '@/components/PedidoCard.vue'
import { meusPedidos, pedidosPendentes } from '@/services/pedidoApi'
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
          <PedidoCard
            v-for="p in pedidos"
            :key="p.id"
            :pedido="p"
            :papel="ehCliente ? 'CLIENTE' : 'AGENTE'"
            :destaque="String(p.id) === pedidoCriado"
            @atualizado="carregarPedidos"
          />
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
