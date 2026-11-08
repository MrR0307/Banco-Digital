import { useState } from 'react'
import './App.css'

function App() {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [mensagem, setMensagem] = useState('')
  const [carregando, setCarregando] = useState(false)
  const [funcionario, setFuncionario] = useState(null)
  const [pagina, setPagina] = useState('Dashboard')
  const [funcionarios, setFuncionarios] = useState([])
  const [carregandoFuncionarios, setCarregandoFuncionarios] = useState(false)
  const [erroFuncionarios, setErroFuncionarios] = useState('')
  const [nomeNovo, setNomeNovo] = useState('')
  const [emailNovo, setEmailNovo] = useState('')
  const [passwordNova, setPasswordNova] = useState('')
  const [mensagemRegisto, setMensagemRegisto] = useState('')
  const [carregandoRegisto, setCarregandoRegisto] = useState(false)
  const [idEditando, setIdEditando] = useState(null)

  async function fazerLogin(e) {
    e.preventDefault()
    setMensagem('')
    setCarregando(true)

    try {
      const resposta = await fetch('http://localhost:8081/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password })
      })

      const dados = await resposta.json()

      if (!resposta.ok) {
        throw new Error(dados.mensagem || 'Email ou palavra-passe incorretos.')
      }

      sessionStorage.setItem('token', dados.token)
      setFuncionario(dados)

    } catch (erro) {
      setMensagem(erro.message || 'Nao foi possivel ligar ao servidor.')
    } finally {
      setCarregando(false)
    }
  }

  function terminarSessao() {
    sessionStorage.removeItem('token')
    setFuncionario(null)
    setEmail('')
    setPassword('')
    setMensagem('')
  }

  async function carregarFuncionarios() {
  setCarregandoFuncionarios(true)
  setErroFuncionarios('')

  try {
    const token = sessionStorage.getItem('token')

    const resposta = await fetch(
      'http://localhost:8081/api/funcionarios',
      {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      }
    )

    if (!resposta.ok) {
      throw new Error('Nao foi possivel carregar os funcionarios.')
    }

    const dados = await resposta.json()
    setFuncionarios(dados)

  } catch (erro) {
    setErroFuncionarios(erro.message)
  } finally {
    setCarregandoFuncionarios(false)
  }
}

async function registarFuncionario(e) {
  e.preventDefault()

  setMensagemRegisto('')
  setCarregandoRegisto(true)

  try {
    const token = sessionStorage.getItem('token')

    const url = idEditando
      ? `http://localhost:8081/api/funcionarios/${idEditando}`
      : 'http://localhost:8081/api/funcionarios'

    const resposta = await fetch(url, {
      method: idEditando ? 'PUT' : 'POST',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        nome: nomeNovo,
        email: emailNovo,
        password: passwordNova
      })
    })

    const dados = await resposta.json()

    if (!resposta.ok) {
      throw new Error(dados.mensagem || 'Erro ao guardar funcionario.')
    }

    setMensagemRegisto(
      idEditando
        ? 'Funcionario atualizado com sucesso!'
        : 'Funcionario registado com sucesso!'
    )

    setNomeNovo('')
    setEmailNovo('')
    setPasswordNova('')
    setIdEditando(null)

    carregarFuncionarios()

  } catch (erro) {
    setMensagemRegisto(erro.message)
  } finally {
    setCarregandoRegisto(false)
  }
}

function prepararEdicao(f) {
  setIdEditando(f.id)
  setNomeNovo(f.nome)
  setEmailNovo(f.email)
  setPasswordNova('')
  setMensagemRegisto('')
}

  if (!funcionario) {
    return (
      <main>
        <div className="login-container">
          <h1>LUXURY BANK</h1>
          <p>Backoffice - Area Administrativa</p>

          <h2>Iniciar Sessão</h2>

          <form onSubmit={fazerLogin}>
            <div>
              <label>Email</label>
              <input
                type="email"
                placeholder="Digite o seu email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
              />
            </div>

            <div>
              <label>Palavra-passe</label>
              <input
                type="password"
                placeholder="Digite a sua palavra-passe"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />
            </div>

            <button type="submit" disabled={carregando}>
              {carregando ? 'A entrar...' : 'Entrar'}
            </button>
          </form>

          {mensagem && <p>{mensagem}</p>}
        </div>
      </main>
    )
  }

  return (
    <div className="dashboard-layout">

      <aside className="sidebar">
        <h1>LUXURY BANK</h1>
        <p className="sidebar-subtitle">BACKOFFICE</p>

        <nav>
          {[
            'Dashboard',
            'Funcionarios',
            'Clientes',
            'Contas bancarias',
            'Transacoes',
            'Cartoes',
            'Relatorios'
          ].map((item) => (
            <button
              key={item}
              className={pagina === item ? 'menu-ativo' : ''}
             onClick={() => {
  setPagina(item)

  if (item === 'Funcionarios') {
    carregarFuncionarios()
  }
}}
            >
              {item}
            </button>
          ))}
        </nav>

        <button className="logout-button" onClick={terminarSessao}>
          Terminar sessao
        </button>
      </aside>

      <section className="dashboard-content">

        <header className="topbar">
          <h2>{pagina}</h2>
          <span>Administrador: {funcionario.nome}</span>
        </header>

        {pagina === 'Dashboard' ? (
  <>
    <div className="welcome">
      <h2>Bem-vindo ao LUXURY BANK</h2>
      <p>Painel de controlo administrativo.</p>
    </div>

    <div className="cards-container">
      <div className="dashboard-card">
        <h3>Funcionarios</h3>
        <p>Gestão de funcionários</p>
        <strong>{funcionarios.length}</strong>
      </div>

      <div className="dashboard-card">
        <h3>Clientes</h3>
        <p>Gestao de clientes</p>
        <strong>0</strong>
      </div>

      <div className="dashboard-card">
        <h3>Contas bancarias</h3>
        <p>Contas registadas</p>
        <strong>0</strong>
      </div>

      <div className="dashboard-card">
        <h3>Transacoes</h3>
        <p>Operacoes realizadas</p>
        <strong>0</strong>
      </div>
    </div>
  </>
) : pagina === 'Funcionarios' ? (
  <div className="welcome">
    <h2>Gestao de Funcionarios</h2>

    <form onSubmit={registarFuncionario} className="form-funcionario">

  <h3>Novo Funcionário</h3>

  <div>
    <label>Nome completo</label>
    <input
      type="text"
      placeholder="Digite o nome completo"
      value={nomeNovo}
      onChange={(e) => setNomeNovo(e.target.value)}
      required
    />
  </div>

  <div>
    <label>Email</label>
    <input
      type="email"
      placeholder="Digite o email"
      value={emailNovo}
      onChange={(e) => setEmailNovo(e.target.value)}
      required
    />
  </div>

  <div>
    <label>Palavra-passe</label>
    <input
  type="password"
  placeholder={idEditando ? 'Nao alterar palavra-passe' : 'Digite a palavra-passe'}
  value={passwordNova}
  onChange={(e) => setPasswordNova(e.target.value)}
  required={!idEditando}
/>
  </div>

  <button type="submit" disabled={carregandoRegisto}>
  {carregandoRegisto
    ? 'A guardar...'
    : idEditando
      ? 'Guardar Alteracoes'
      : 'Registar Funcionario'}
</button>
{idEditando && (
  <button
    type="button"
    onClick={() => {
      setIdEditando(null)
      setNomeNovo('')
      setEmailNovo('')
      setPasswordNova('')
      setMensagemRegisto('')
    }}
  >
    Cancelar
  </button>
)}
  {mensagemRegisto && <p>{mensagemRegisto}</p>}

</form>

    {carregandoFuncionarios && <p>A carregar funcionarios...</p>}

    {erroFuncionarios && <p>{erroFuncionarios}</p>}

    {!carregandoFuncionarios && !erroFuncionarios && (
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>Nome</th>
            <th>Email</th>
            <th>Estado</th>
            <th>Acoes</th>
          </tr>
        </thead>

        <tbody>
          {funcionarios.map((f) => (
            <tr key={f.id}>
              <td>{f.id}</td>
              <td>{f.nome}</td>
              <td>{f.email}</td>
              <td>{f.estado ? 'Ativo' : 'Inativo'}</td>
              <td>
  <button onClick={() => prepararEdicao(f)}>
    Editar
  </button>
</td>
            </tr>
          ))}
        </tbody>
      </table>
    )}
  </div>
) : (
  <div className="welcome">
    <h2>{pagina}</h2>
    <p>Este modulo sera desenvolvido na proxima etapa.</p>
  </div>
)}

      </section>
    </div>
  )
}

export default App