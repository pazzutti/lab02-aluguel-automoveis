const BASE_URL = '/api/contratos'

// Espelha o enum SituacaoContrato do backend.
export const SITUACOES_CONTRATO = {
  PENDENTE: { rotulo: 'Pendente', descricao: 'Aguardando o banco conceder o crédito.' },
  EM_EXECUCAO: { rotulo: 'Em execução', descricao: 'Contrato ativo.' },
  ENCERRADO: { rotulo: 'Encerrado', descricao: 'Contrato finalizado.' },
}

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

export function meusContratos() {
  return fetch(BASE_URL).then(tratarResposta)
}

export function locacoesAtivas() {
  return fetch(`${BASE_URL}/locacoes-ativas`).then(tratarResposta)
}

export function registrarDevolucao(id, data) {
  return fetch(`${BASE_URL}/${id}/devolucao`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ data }),
  }).then(tratarResposta)
}

export function leasingsPendentes() {
  return fetch(`${BASE_URL}/leasings-pendentes`).then(tratarResposta)
}

export function concederCredito(id, valor) {
  return fetch(`${BASE_URL}/${id}/credito`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ valor }),
  }).then(tratarResposta)
}
