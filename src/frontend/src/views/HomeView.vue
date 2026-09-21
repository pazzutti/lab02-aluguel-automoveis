<script setup>
import { authStore } from '@/store/auth'
</script>

<template>
  <main class="home">
    <h1>Xulambs Aluguel de Automóveis</h1>
    <p v-if="authStore.usuario">
      Bem-vindo(a) de volta, <strong>{{ authStore.usuario.login }}</strong>.
    </p>
    <p v-else>
      Sistema de gestão de aluguéis de automóveis. Crie uma conta ou entre para continuar.
    </p>
    <p v-if="authStore.usuario && authStore.usuario.tipo !== 'CLIENTE'">
      Ainda não há nada por aqui para contas do tipo {{ authStore.usuario.tipo.toLowerCase() }}.
    </p>

    <div class="acoes" v-if="authStore.usuario?.tipo === 'CLIENTE'">
      <RouterLink class="botao" to="/perfil">Meu perfil</RouterLink>
    </div>
    <div class="acoes" v-else-if="!authStore.usuario">
      <RouterLink class="botao" to="/registrar">Criar conta</RouterLink>
      <RouterLink class="botao secundario" to="/login">Entrar</RouterLink>
    </div>
  </main>
</template>

<style scoped>
.home {
  max-width: 640px;
  margin: 0 auto;
  padding: 3rem 1rem;
  text-align: center;
}

.home p {
  margin-top: 1rem;
  opacity: 0.85;
}

.acoes {
  display: flex;
  gap: 0.75rem;
  justify-content: center;
  margin-top: 2rem;
}

.botao {
  display: inline-block;
  padding: 0.6rem 1.2rem;
  border-radius: 4px;
  text-decoration: none;
  background: hsla(160, 100%, 37%, 1);
  color: #fff;
}

.botao.secundario {
  background: var(--color-background-mute);
  color: var(--color-text);
}
</style>
