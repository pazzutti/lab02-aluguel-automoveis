<script setup>
import { ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { atualizarMinhaConta, excluirMinhaConta, minhaConta } from '@/services/clienteApi'
import { authStore, sair } from '@/store/auth'

const MAX_EMPREGADORAS = 3

const router = useRouter()
const cliente = ref(null)
const form = ref(null)
const carregando = ref(false)
const erro = ref('')
const mensagem = ref('')

function empregadorasParaFormulario(lista) {
  const empregadoras = Array.from({ length: MAX_EMPREGADORAS }, () => ({ nome: '', rendimento: '' }))
  lista.forEach((emp, i) => {
    if (i < MAX_EMPREGADORAS) {
      empregadoras[i] = { nome: emp.nome, rendimento: String(emp.rendimento) }
    }
  })
  return empregadoras
}

async function carregarPerfil() {
  carregando.value = true
  erro.value = ''
  try {
    cliente.value = await minhaConta()
    form.value = {
      endereco: cliente.value.endereco,
      profissao: cliente.value.profissao,
      empregadoras: empregadorasParaFormulario(cliente.value.empregadoras),
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
    if (usuario && usuario.tipo === 'CLIENTE') {
      carregarPerfil()
    }
  },
  { immediate: true },
)

function empregadorasParaEnvio() {
  return form.value.empregadoras
    .filter((emp) => emp.nome.trim() && emp.rendimento !== '')
    .map((emp) => ({ nome: emp.nome, rendimento: Number(emp.rendimento) }))
}

async function salvar() {
  erro.value = ''
  mensagem.value = ''
  try {
    cliente.value = await atualizarMinhaConta({
      endereco: form.value.endereco,
      profissao: form.value.profissao,
      empregadoras: empregadorasParaEnvio(),
    })
    form.value.empregadoras = empregadorasParaFormulario(cliente.value.empregadoras)
    mensagem.value = 'Dados atualizados com sucesso.'
  } catch (e) {
    erro.value = e.message
  }
}

async function excluirConta() {
  if (!confirm('Excluir sua conta? Essa ação não pode ser desfeita.')) {
    return
  }
  erro.value = ''
  try {
    await excluirMinhaConta()
    await sair()
    router.push('/')
  } catch (e) {
    erro.value = e.message
  }
}
</script>

<template>
  <main class="perfil">
    <h1>Meu perfil</h1>

    <template v-if="!authStore.usuario">
      <p class="nota">Você precisa <RouterLink to="/login">entrar</RouterLink> para ver seu perfil.</p>
    </template>
    <template v-else-if="authStore.usuario.tipo !== 'CLIENTE'">
      <p class="nota">Esta conta ({{ authStore.usuario.tipo }}) não tem um perfil de cliente.</p>
    </template>
    <template v-else>
      <p v-if="carregando">Carregando...</p>
      <template v-else-if="cliente && form">
        <p v-if="mensagem" class="aviso aviso-sucesso">{{ mensagem }}</p>
        <p v-if="erro" class="aviso aviso-erro">{{ erro }}</p>

        <form @submit.prevent="salvar">
          <fieldset class="secao">
            <legend>Dados cadastrais</legend>
            <div class="campos">
              <label>
                Nome
                <input :value="cliente.nome" disabled />
              </label>
              <label>
                RG
                <input :value="cliente.rg" disabled />
              </label>
              <label>
                CPF
                <input :value="cliente.cpf" disabled />
              </label>
              <label>
                Endereço
                <input v-model="form.endereco" required />
              </label>
              <label>
                Profissão
                <input v-model="form.profissao" required />
              </label>
            </div>
          </fieldset>

          <fieldset class="secao">
            <legend>Entidades empregadoras (até {{ MAX_EMPREGADORAS }})</legend>
            <div v-for="(emp, i) in form.empregadoras" :key="i" class="linha-empregadora">
              <input v-model="emp.nome" :placeholder="`Empregadora ${i + 1}`" />
              <input v-model="emp.rendimento" type="number" step="0.01" min="0" placeholder="Rendimento" />
            </div>
            <p class="rendimento-total">Rendimento total: {{ cliente.rendimentoTotal }}</p>
          </fieldset>

          <button type="submit">Salvar alterações</button>
        </form>

        <div class="zona-perigo">
          <h2>Excluir conta</h2>
          <p>Isso remove seu cadastro permanentemente.</p>
          <button type="button" class="perigo" @click="excluirConta">Excluir minha conta</button>
        </div>
      </template>
    </template>
  </main>
</template>

<style scoped>
.perfil {
  max-width: 640px;
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

.campos {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 0.75rem;
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

input:disabled {
  opacity: 0.65;
}

.linha-empregadora {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 0.5rem;
  margin-top: 0.5rem;
}

.rendimento-total {
  margin-top: 0.75rem;
  font-size: 0.9rem;
  opacity: 0.8;
}

button {
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 4px;
  background: hsla(160, 100%, 37%, 1);
  color: #fff;
  cursor: pointer;
}

button.perigo {
  background: hsl(0, 70%, 50%);
}

.zona-perigo {
  margin-top: 2rem;
  padding-top: 1rem;
  border-top: 1px solid var(--color-border);
}

.zona-perigo h2 {
  font-size: 1rem;
}

.zona-perigo p {
  font-size: 0.85rem;
  opacity: 0.8;
  margin: 0.3rem 0 0.75rem;
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
