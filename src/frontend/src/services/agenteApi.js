const BASE_URL = '/api'

async function tratarResposta(response) {
  const corpo = await response.json().catch(() => null)
  if (!response.ok) {
    throw new Error(corpo?.mensagem ?? 'Erro ao comunicar com o servidor')
  }
  return corpo
}

export function cadastrarEmpresa(empresa) {
  return fetch(`${BASE_URL}/empresas`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(empresa),
  }).then(tratarResposta)
}

export function cadastrarBanco(banco) {
  return fetch(`${BASE_URL}/bancos`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(banco),
  }).then(tratarResposta)
}
