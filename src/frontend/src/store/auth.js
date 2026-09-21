import { reactive } from 'vue'
import { login as loginApi, logout as logoutApi, usuarioAtual } from '@/services/authApi'

// Estado de sessao compartilhado entre o cabecalho (App.vue), a tela de
// login e a tela de clientes, para nao repetir a checagem de /api/me em
// cada componente.
export const authStore = reactive({
  usuario: null,
  carregado: false,
})

export async function inicializarAuth() {
  authStore.usuario = await usuarioAtual()
  authStore.carregado = true
}

export async function entrar(login, senha) {
  await loginApi(login, senha)
  authStore.usuario = await usuarioAtual()
}

export async function sair() {
  await logoutApi()
  authStore.usuario = null
}
