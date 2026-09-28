<script setup>
import { reactive, ref, watch } from 'vue'
import { cadastrarAutomovel, meusAutomoveis } from '@/services/automovelApi'
import { authStore } from '@/store/auth'

const automoveis = ref([])
const carregando = ref(false)
const erro = ref('')
const mensagem = ref('')
const enviando = ref(false)

const form = reactive({ placa: '', ano: '', marca: '', modelo: '' })

async function carregar() {
  carregando.value = true
  erro.value = ''
  try {
    automoveis.value = await meusAutomoveis()
  } catch (e) {
    erro.value = e.message
  } finally {
    carregando.value = false
  }
}

watch(
  () => authStore.usuario,
  (usuario) => {
    if (usuario?.tipo === 'EMPRESA') {
      carregar()
    }
  },
  { immediate: true },
)

async function cadastrar() {
  erro.value = ''
  mensagem.value = ''
  enviando.value = true
  try {
    await cadastrarAutomovel({ ...form, ano: Number(form.ano) })
    form.placa = ''
    form.ano = ''
    form.marca = ''
    form.modelo = ''
    mensagem.value = 'Automóvel cadastrado com sucesso.'
    await carregar()
  } catch (e) {
    erro.value = e.message
  } finally {
    enviando.value = false
  }
}
</script>

<template>
  <main class="automoveis">
    <template v-if="!authStore.usuario">
      <h1>Automóveis</h1>
      <p class="nota">Você precisa <RouterLink to="/login">entrar</RouterLink> para ver esta página.</p>
    </template>
    <template v-else-if="authStore.usuario.tipo !== 'EMPRESA'">
      <h1>Automóveis</h1>
      <p class="nota">Esta conta ({{ authStore.usuario.tipo }}) não gerencia uma frota de automóveis.</p>
    </template>
    <template v-else>
      <h1>Minha frota</h1>

      <p v-if="mensagem" class="aviso aviso-sucesso">{{ mensagem }}</p>
      <p v-if="erro" class="aviso aviso-erro">{{ erro }}</p>

      <form class="secao" @submit.prevent="cadastrar">
        <fieldset>
          <legend>Cadastrar automóvel</legend>
          <div class="campos">
            <label>
              Placa
              <input v-model="form.placa" required />
            </label>
            <label>
              Ano
              <input v-model="form.ano" type="number" min="1900" required />
            </label>
            <label>
              Marca
              <input v-model="form.marca" required />
            </label>
            <label>
              Modelo
              <input v-model="form.modelo" required />
            </label>
          </div>
          <button type="submit" :disabled="enviando">Cadastrar</button>
        </fieldset>
      </form>

      <h2>Automóveis cadastrados</h2>
      <p v-if="carregando">Carregando...</p>
      <p v-else-if="automoveis.length === 0" class="nota">Nenhum automóvel cadastrado ainda.</p>
      <ul v-else class="lista">
        <li v-for="a in automoveis" :key="a.id" class="automovel">
          <span class="modelo">{{ a.marca }} {{ a.modelo }} {{ a.ano }}</span>
          <span class="placa">{{ a.placa }}</span>
          <span class="disponibilidade" :class="{ indisponivel: !a.disponivel }">
            {{ a.disponivel ? 'Disponível' : 'Indisponível' }}
          </span>
        </li>
      </ul>
    </template>
  </main>
</template>

<style scoped>
.automoveis {
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
  margin-bottom: 1.5rem;
}

.secao legend {
  font-weight: bold;
  padding: 0 0.4rem;
}

.campos {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));
  gap: 0.75rem;
  margin-bottom: 1rem;
}

label {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
  font-size: 0.9rem;
}

input {
  padding: 0.45rem 0.6rem;
  border: 1px solid var(--color-border);
  border-radius: 4px;
  background: var(--color-background);
  color: var(--color-text);
}

button {
  padding: 0.5rem 1rem;
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

h2 {
  font-size: 1.05rem;
  margin-bottom: 0.75rem;
}

.lista {
  list-style: none;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.automovel {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  border: 1px solid var(--color-border);
  border-radius: 6px;
  padding: 0.6rem 0.9rem;
  font-size: 0.9rem;
}

.modelo {
  font-weight: bold;
  color: var(--color-heading);
}

.placa {
  padding: 0.05rem 0.4rem;
  border: 1px solid var(--color-border);
  border-radius: 3px;
  font-family: monospace;
  font-size: 0.85rem;
}

.disponibilidade {
  margin-left: auto;
  font-size: 0.8rem;
  color: hsl(160, 100%, 25%);
}

.disponibilidade.indisponivel {
  color: hsl(0, 80%, 45%);
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
