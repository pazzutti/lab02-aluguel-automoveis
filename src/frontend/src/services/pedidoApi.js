const BASE_URL = '/api/pedidos'

// Valores aceitos pelo backend (enum ModalidadeContrato) com o rotulo exibido.
export const MODALIDADES = [
  { valor: 'LOCACAO', rotulo: 'Locação', descricao: 'Aluguel por prazo determinado, com data de devolução.' },
  { valor: 'ASSINATURA', rotulo: 'Assinatura', descricao: 'Mensalidade recorrente enquanto durar o contrato.' },
  { valor: 'LEASING', rotulo: 'Leasing', descricao: 'Longo prazo, com crédito concedido por um banco.' },
]

export function rotuloModalidade(valor) {
  return MODALIDADES.find((m) => m.valor === valor)?.rotulo ?? valor
}

// Espelha o enum SituacaoPedido do backend (RF24).
export const SITUACOES = {
  NAO_AVALIADO: { rotulo: 'Não avaliado', descricao: 'Aguardando um agente iniciar a análise.' },
  EM_ANALISE: { rotulo: 'Em análise', descricao: 'Um agente está avaliando o pedido.' },
  AVALIADO: { rotulo: 'Avaliado', descricao: 'O parecer do agente foi registrado.' },
  ACEITO: { rotulo: 'Aceito', descricao: 'Pedido aceito; segue para a geração do contrato.' },
  RECUSADO: { rotulo: 'Recusado', descricao: 'Pedido encerrado sem contrato.' },
  CANCELADO: { rotulo: 'Cancelado', descricao: 'Pedido cancelado pelo cliente.' },
}

// Espelha o enum ResultadoParecer do backend.
export const RESULTADOS = {
  FAVORAVEL: { rotulo: 'Favorável' },
  DESFAVORAVEL: { rotulo: 'Desfavorável' },
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

export function criarPedido(pedido) {
  return fetch(BASE_URL, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(pedido),
  }).then(tratarResposta)
}

export function meusPedidos() {
  return fetch(BASE_URL).then(tratarResposta)
}

export function pedidosPendentes() {
  return fetch(`${BASE_URL}/pendentes`).then(tratarResposta)
}

export function alterarPedido(id, pedido) {
  return fetch(`${BASE_URL}/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(pedido),
  }).then(tratarResposta)
}

export function cancelarPedido(id) {
  return fetch(`${BASE_URL}/${id}/cancelar`, { method: 'POST' }).then(tratarResposta)
}

export function iniciarAnalise(id) {
  return fetch(`${BASE_URL}/${id}/analise`, { method: 'POST' }).then(tratarResposta)
}

export function registrarParecer(id, parecer) {
  return fetch(`${BASE_URL}/${id}/parecer`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(parecer),
  }).then(tratarResposta)
}

export function decidirPedido(id, decisao) {
  return fetch(`${BASE_URL}/${id}/decisao`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(decisao),
  }).then(tratarResposta)
}
