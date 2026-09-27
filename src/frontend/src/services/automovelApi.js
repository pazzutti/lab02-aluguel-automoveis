const BASE_URL = '/api/automoveis'

async function tratarResposta(response) {
  if (response.status === 401) {
    throw new Error('Sessão expirada ou inexistente. Faça login antes de usar o sistema.')
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
