const BASE_URL = '/api/clientes'

async function tratarResposta(response) {
  if (response.status === 204) {
    return null
  }
  if (response.status === 401) {
    throw new Error('Sessão expirada ou inexistente. Faça login em /login antes de usar o sistema.')
  }
  if (response.status === 403) {
    throw new Error('Esta conta não tem acesso a um perfil de cliente.')
  }
  const corpo = await response.json().catch(() => null)
  if (!response.ok) {
    throw new Error(corpo?.mensagem ?? 'Erro ao comunicar com o servidor')
  }
  return corpo
}

export function cadastrarCliente(cliente) {
  return fetch(BASE_URL, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(cliente),
  }).then(tratarResposta)
}

export function minhaConta() {
  return fetch(`${BASE_URL}/me`).then(tratarResposta)
}

export function atualizarMinhaConta(cliente) {
  return fetch(`${BASE_URL}/me`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(cliente),
  }).then(tratarResposta)
}

export function excluirMinhaConta() {
  return fetch(`${BASE_URL}/me`, { method: 'DELETE' }).then(tratarResposta)
}
