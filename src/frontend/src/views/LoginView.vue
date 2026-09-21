<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { entrar as entrarStore } from '@/store/auth'

const router = useRouter()
const form = ref({ login: '', senha: '' })
const erro = ref('')
const enviando = ref(false)

async function entrar() {
  erro.value = ''
  enviando.value = true
  try {
    await entrarStore(form.value.login, form.value.senha)
    router.push('/')
  } catch (e) {
    erro.value = e.message
  } finally {
    enviando.value = false
  }
}
</script>

<template>
  <main class="login">
    <h1>Entrar</h1>

    <p v-if="erro" class="aviso aviso-erro">{{ erro }}</p>

    <form @submit.prevent="entrar">
      <label>
        Login
        <input v-model="form.login" required autofocus />
      </label>
      <label>
        Senha
        <input v-model="form.senha" type="password" required />
      </label>
      <button type="submit" :disabled="enviando">Entrar</button>
    </form>

    <p class="nota">Ainda não tem conta? <RouterLink to="/registrar">Criar conta</RouterLink></p>
  </main>
</template>

<style scoped>
.login {
  max-width: 360px;
  margin: 0 auto;
  padding: 2rem 1rem;
}

label {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
  font-size: 0.9rem;
  margin-bottom: 1rem;
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

.nota {
  margin-top: 1.5rem;
  font-size: 0.9rem;
}

.aviso-erro {
  background: hsla(0, 90%, 50%, 0.12);
  color: hsl(0, 80%, 45%);
  padding: 0.6rem 1rem;
  border-radius: 4px;
  margin-bottom: 1rem;
}
</style>
