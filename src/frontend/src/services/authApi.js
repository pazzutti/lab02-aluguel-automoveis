export async function login(login, senha) {
  const corpo = new URLSearchParams()
  corpo.set('username', login)
  corpo.set('password', senha)

  const response = await fetch('/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    body: corpo,
  })
  if (!response.ok) {
    throw new Error('Login ou senha inválidos')
  }
}

export async function logout() {
  await fetch('/logout', { method: 'POST' })
}

export async function usuarioAtual() {
  const response = await fetch('/api/me')
  if (!response.ok) {
    return null
  }
  return response.json()
}
