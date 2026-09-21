<script setup>
import { reactive, ref } from 'vue'
import { cadastrarCliente } from '@/services/clienteApi'
import { cadastrarBanco, cadastrarEmpresa } from '@/services/agenteApi'

const MAX_EMPREGADORAS = 3

const TIPOS = [
  { valor: 'CLIENTE', rotulo: 'Cliente' },
  { valor: 'EMPRESA', rotulo: 'Empresa' },
  { valor: 'BANCO', rotulo: 'Banco' },
]

const tipo = ref('CLIENTE')
const erro = ref('')
const mensagem = ref('')
const enviando = ref(false)

const acesso = reactive({ login: '', senha: '' })

const dadosCliente = reactive({
  rg: '',
  cpf: '',
  nome: '',
  endereco: '',
  profissao: '',
  empregadoras: Array.from({ length: MAX_EMPREGADORAS }, () => ({ nome: '', rendimento: '' })),
})

const dadosAgente = reactive({ nomeInstituicao: '' })

function empregadorasParaEnvio() {
  return dadosCliente.empregadoras
    .filter((emp) => emp.nome.trim() && emp.rendimento !== '')
    .map((emp) => ({ nome: emp.nome, rendimento: Number(emp.rendimento) }))
}

async function registrar() {
  erro.value = ''
  mensagem.value = ''
  enviando.value = true
  try {
    if (tipo.value === 'CLIENTE') {
      await cadastrarCliente({
        rg: dadosCliente.rg,
        cpf: dadosCliente.cpf,
        nome: dadosCliente.nome,
        endereco: dadosCliente.endereco,
        profissao: dadosCliente.profissao,
        login: acesso.login,
        senha: acesso.senha,
        empregadoras: empregadorasParaEnvio(),
      })
    } else if (tipo.value === 'EMPRESA') {
      await cadastrarEmpresa({
        nomeInstituicao: dadosAgente.nomeInstituicao,
        login: acesso.login,
        senha: acesso.senha,
      })
    } else {
      await cadastrarBanco({
        nomeInstituicao: dadosAgente.nomeInstituicao,
        login: acesso.login,
        senha: acesso.senha,
      })
    }
    mensagem.value = 'Conta criada com sucesso.'
    acesso.login = ''
    acesso.senha = ''
  } catch (e) {
    erro.value = e.message
  } finally {
    enviando.value = false
  }
}
</script>

<template>
  <main class="registrar">
    <h1>Criar conta</h1>

    <p v-if="mensagem" class="aviso aviso-sucesso">
      {{ mensagem }} <RouterLink to="/login">Ir para o login</RouterLink>
    </p>
    <p v-if="erro" class="aviso aviso-erro">{{ erro }}</p>

    <form @submit.prevent="registrar">
      <fieldset class="secao">
        <legend>Tipo de conta</legend>
        <div class="tipos">
          <label v-for="t in TIPOS" :key="t.valor" class="opcao-tipo" :class="{ selecionada: tipo === t.valor }">
            <input type="radio" v-model="tipo" :value="t.valor" name="tipo" />
            {{ t.rotulo }}
          </label>
        </div>
      </fieldset>

      <fieldset class="secao">
        <legend>Dados de acesso</legend>
        <div class="campos">
          <label>
            Login
            <input v-model="acesso.login" required />
          </label>
          <label>
            Senha
            <input v-model="acesso.senha" type="password" minlength="6" required />
          </label>
        </div>
      </fieldset>

      <fieldset v-if="tipo === 'CLIENTE'" class="secao">
        <legend>Dados do cliente</legend>
        <div class="campos">
          <label>
            RG
            <input v-model="dadosCliente.rg" required />
          </label>
          <label>
            CPF
            <input v-model="dadosCliente.cpf" required />
          </label>
          <label>
            Nome
            <input v-model="dadosCliente.nome" required />
          </label>
          <label>
            Endereço
            <input v-model="dadosCliente.endereco" required />
          </label>
          <label>
            Profissão
            <input v-model="dadosCliente.profissao" required />
          </label>
        </div>

        <fieldset class="empregadoras">
          <legend>Entidades empregadoras (até {{ MAX_EMPREGADORAS }}, opcional)</legend>
          <div v-for="(emp, i) in dadosCliente.empregadoras" :key="i" class="linha-empregadora">
            <input v-model="emp.nome" :placeholder="`Empregadora ${i + 1}`" />
            <input v-model="emp.rendimento" type="number" step="0.01" min="0" placeholder="Rendimento" />
          </div>
        </fieldset>
      </fieldset>

      <fieldset v-else class="secao">
        <legend>Dados d{{ tipo === 'EMPRESA' ? 'a empresa' : 'o banco' }}</legend>
        <div class="campos">
          <label>
            Nome da instituição
            <input v-model="dadosAgente.nomeInstituicao" required />
          </label>
        </div>
      </fieldset>

      <button type="submit" :disabled="enviando">Criar conta</button>
    </form>

    <p class="nota">Já tem conta? <RouterLink to="/login">Entrar</RouterLink></p>
  </main>
</template>

<style scoped>
.registrar {
  max-width: 640px;
  margin: 0 auto;
  padding: 2rem 1rem;
}

.secao {
  border: 1px solid var(--color-border);
  border-radius: 8px;
  padding: 1rem 1.25rem;
  margin-bottom: 1.25rem;
}

.secao legend {
  font-weight: bold;
  padding: 0 0.4rem;
}

.tipos {
  display: flex;
  gap: 0.5rem;
}

.opcao-tipo {
  display: flex;
  align-items: center;
  gap: 0.4rem;
  padding: 0.5rem 0.9rem;
  border: 1px solid var(--color-border);
  border-radius: 999px;
  cursor: pointer;
  font-size: 0.9rem;
}

.opcao-tipo.selecionada {
  border-color: hsla(160, 100%, 37%, 1);
  background: hsla(160, 100%, 37%, 0.12);
}

.opcao-tipo input {
  margin: 0;
}

.campos {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 0.75rem;
}

label {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
  font-size: 0.9rem;
}

input {
  padding: 0.45rem 0.6rem;
  border: 1px solid var(--color-border);
  border-radius: 4px;
  background: var(--color-background);
  color: var(--color-text);
}

.empregadoras {
  border: 1px solid var(--color-border);
  border-radius: 6px;
  padding: 0.75rem;
  margin-top: 1rem;
}

.linha-empregadora {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 0.5rem;
  margin-top: 0.5rem;
}

button {
  padding: 0.6rem 1.2rem;
  border: none;
  border-radius: 4px;
  background: hsla(160, 100%, 37%, 1);
  color: #fff;
  cursor: pointer;
}

.nota {
  margin-top: 1.5rem;
  font-size: 0.9rem;
}

.aviso {
  padding: 0.6rem 1rem;
  border-radius: 4px;
  margin-bottom: 1rem;
}

.aviso-sucesso {
  background: hsla(160, 100%, 37%, 0.15);
  color: hsl(160, 100%, 25%);
}

.aviso-erro {
  background: hsla(0, 90%, 50%, 0.12);
  color: hsl(0, 80%, 45%);
}
</style>
