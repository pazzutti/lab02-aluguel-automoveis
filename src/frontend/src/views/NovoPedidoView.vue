<script setup>
import { ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { listarAutomoveisDisponiveis } from '@/services/automovelApi'
import { MODALIDADES, criarPedido } from '@/services/pedidoApi'
import { authStore } from '@/store/auth'

const router = useRouter()
const automoveis = ref([])
const automovelId = ref(null)
const modalidade = ref('LOCACAO')
const carregando = ref(false)
const enviando = ref(false)
const erro = ref('')

async function carregarAutomoveis() {
  carregando.value = true
  erro.value = ''
  try {
    automoveis.value = await listarAutomoveisDisponiveis()
  } catch (e) {
    erro.value = e.message
  } finally {
    carregando.value = false
  }
}

watch(
  () => authStore.usuario,
  (usuario) => {
    if (usuario && usuario.tipo === 'CLIENTE') {
      carregarAutomoveis()
    }
  },
  { immediate: true },
)

async function enviar() {
  erro.value = ''
  enviando.value = true
  try {
    const pedido = await criarPedido({ automovelId: automovelId.value, modalidade: modalidade.value })
    router.push({ path: '/pedidos', query: { criado: pedido.id } })
  } catch (e) {
    erro.value = e.message
  } finally {
    enviando.value = false
  }
}
</script>

<template>
  <main class="novo-pedido">
    <h1>Novo pedido de aluguel</h1>

    <template v-if="!authStore.usuario">
      <p class="nota">Você precisa <RouterLink to="/login">entrar</RouterLink> para fazer um pedido.</p>
    </template>
    <template v-else-if="authStore.usuario.tipo !== 'CLIENTE'">
      <p class="nota">Apenas clientes podem criar pedidos de aluguel.</p>
    </template>
    <template v-else>
      <p v-if="erro" class="aviso aviso-erro">{{ erro }}</p>

      <form @submit.prevent="enviar">
        <fieldset class="secao">
          <legend>1. Escolha o automóvel</legend>
          <p v-if="carregando">Carregando automóveis...</p>
          <p v-else-if="automoveis.length === 0" class="nota">Nenhum automóvel disponível no momento.</p>
          <div v-else class="automoveis">
            <label
              v-for="a in automoveis"
              :key="a.id"
              class="opcao-automovel"
              :class="{ selecionada: automovelId === a.id }"
            >
              <input type="radio" v-model="automovelId" :value="a.id" name="automovel" required />
              <span class="modelo">{{ a.marca }} {{ a.modelo }}</span>
              <span class="detalhe">{{ a.ano }} · placa {{ a.placa }}</span>
            </label>
          </div>
        </fieldset>

        <fieldset class="secao">
          <legend>2. Escolha a modalidade</legend>
          <div class="modalidades">
            <label
              v-for="m in MODALIDADES"
              :key="m.valor"
              class="opcao-modalidade"
              :class="{ selecionada: modalidade === m.valor }"
            >
              <input type="radio" v-model="modalidade" :value="m.valor" name="modalidade" />
              <span>
                <strong>{{ m.rotulo }}</strong>
                <small>{{ m.descricao }}</small>
              </span>
            </label>
          </div>
        </fieldset>

        <div class="acoes">
          <button type="submit" :disabled="enviando || !automovelId">Enviar pedido</button>
          <RouterLink to="/pedidos">Voltar para meus pedidos</RouterLink>
        </div>
      </form>
    </template>
  </main>
</template>

<style scoped>
.novo-pedido {
  max-width: 720px;
  margin: 0 auto;
  padding: 2rem 1rem;
}

.nota {
  font-size: 0.9rem;
}

.secao {
  border: 1px solid var(--color-border);
  border-radius: 8px;
  padding: 1rem 1.25rem;
  margin-bottom: 1.25rem;
}

.secao legend {
  font-weight: bold;
  padding: 0 0.4rem;
}

.automoveis {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 0.6rem;
}

.opcao-automovel,
.opcao-modalidade {
  border: 1px solid var(--color-border);
  border-radius: 6px;
  padding: 0.6rem 0.8rem;
  cursor: pointer;
}

.opcao-automovel {
  display: flex;
  flex-direction: column;
}

.opcao-automovel input {
  display: none;
}

.opcao-automovel .modelo {
  font-weight: bold;
  color: var(--color-heading);
}

.opcao-automovel .detalhe {
  font-size: 0.85rem;
  opacity: 0.8;
}

.selecionada {
  border-color: hsla(160, 100%, 37%, 1);
  background: hsla(160, 100%, 37%, 0.12);
}

.modalidades {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.opcao-modalidade {
  display: flex;
  align-items: flex-start;
  gap: 0.6rem;
}

.opcao-modalidade input {
  margin-top: 0.3rem;
}

.opcao-modalidade small {
  display: block;
  font-size: 0.8rem;
  opacity: 0.8;
}

.acoes {
  display: flex;
  align-items: center;
  gap: 1rem;
  flex-wrap: wrap;
  font-size: 0.9rem;
}

button {
  padding: 0.6rem 1.2rem;
  border: none;
  border-radius: 4px;
  background: hsla(160, 100%, 37%, 1);
  color: #fff;
  cursor: pointer;
}

button:disabled {
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
