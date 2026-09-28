const BASE_URL = '/api/automoveis'

async function tratarResposta(response) {
  if (response.status === 401) {
    throw new Error('Sessão expirada ou inexistente. Faça login antes de usar o sistema.')
  }
  if (response.status === 403) {
    throw new Error('Esta conta não tem acesso a esta funcionalidade.')
  }
  const corpo = await response.json().catch(() => null)
  if (!response.ok) {
    throw new Error(corpo?.mensagem ?? 'Erro ao comunicar com o servidor')
  }
  return corpo
}

export function listarAutomoveisDisponiveis() {
  return fetch(BASE_URL).then(tratarResposta)
}

export function cadastrarAutomovel(automovel) {
  return fetch(BASE_URL, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(automovel),
  }).then(tratarResposta)
}

export function meusAutomoveis() {
  return fetch(`${BASE_URL}/meus`).then(tratarResposta)
}
