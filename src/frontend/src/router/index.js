import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import ProfileView from '../views/ProfileView.vue'
import LoginView from '../views/LoginView.vue'
import RegisterView from '../views/RegisterView.vue'
import PedidosView from '../views/PedidosView.vue'
import NovoPedidoView from '../views/NovoPedidoView.vue'
import AutomoveisView from '../views/AutomoveisView.vue'
import ContratosView from '../views/ContratosView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    {
      path: '/perfil',
      name: 'perfil',
      component: ProfileView,
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView,
    },
    {
      path: '/registrar',
      name: 'registrar',
      component: RegisterView,
    },
    {
      path: '/pedidos',
      name: 'pedidos',
      component: PedidosView,
    },
    {
      path: '/pedidos/novo',
      name: 'novo-pedido',
      component: NovoPedidoView,
    },
    {
      path: '/automoveis',
      name: 'automoveis',
      component: AutomoveisView,
    },
    {
      path: '/contratos',
      name: 'contratos',
      component: ContratosView,
    },
  ],
})

export default router
