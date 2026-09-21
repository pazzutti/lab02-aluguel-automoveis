<script setup>
import { onMounted } from 'vue'
import { RouterLink, RouterView, useRouter } from 'vue-router'
import { authStore, inicializarAuth, sair } from '@/store/auth'

const router = useRouter()

onMounted(inicializarAuth)

async function sairEVoltar() {
  await sair()
  router.push('/')
}
</script>

<template>
  <header class="cabecalho">
    <RouterLink class="titulo" to="/">Xulambs Aluguel de Automóveis</RouterLink>

    <nav>
      <RouterLink v-if="authStore.usuario?.tipo === 'CLIENTE'" to="/perfil">Meu perfil</RouterLink>

      <template v-if="authStore.usuario">
        <button type="button" class="link" @click="sairEVoltar">Sair</button>
      </template>
      <template v-else>
        <RouterLink to="/login">Entrar</RouterLink>
        <RouterLink class="botao" to="/registrar">Criar conta</RouterLink>
      </template>
    </nav>
  </header>

  <RouterView />
</template>

<style scoped>
.cabecalho {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 1rem;
  padding: 1rem 2rem;
  border-bottom: 1px solid var(--color-border);
}

.titulo {
  font-size: 1.1rem;
  font-weight: bold;
  color: var(--color-heading);
  text-decoration: none;
}

nav {
  display: flex;
  align-items: center;
  gap: 1rem;
  font-size: 0.9rem;
}

nav a {
  color: var(--color-text);
  text-decoration: none;
}

nav a.router-link-exact-active {
  color: hsla(160, 100%, 37%, 1);
}

.botao {
  padding: 0.35rem 0.8rem;
  border-radius: 4px;
  background: hsla(160, 100%, 37%, 1);
  color: #fff !important;
}

button.link {
  background: none;
  border: none;
  padding: 0;
  color: var(--color-text);
  text-decoration: underline;
  cursor: pointer;
  font-size: 0.9rem;
}
</style>
