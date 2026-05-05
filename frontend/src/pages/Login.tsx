import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import api from '../api'
import type { AuthResponse, ErrorResponse } from '../types'

export default function Login() {
  const [tab, setTab] = useState<'login' | 'register'>('login')
  const [email, setEmail] = useState<string>('')
  const [password, setPassword] = useState<string>('')
  const [loading, setLoading] = useState<boolean>(false)
  const [error, setError] = useState<string>('')
  const [success, setSuccess] = useState<string>('')
  const navigate = useNavigate()

  const reset = () => { setError(''); setSuccess('') }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    reset()
    if (!email || !password) { setError('Email and password are required'); return }
    setLoading(true)
    try {
      if (tab === 'login') {
        const res = await api.post<AuthResponse>('/v1/login', { email, password })
        localStorage.setItem('token', res.data.token)
        navigate('/')
      } else {
        await api.post('/v1/register', { email, password })
        setSuccess('Account created! You can now sign in.')
        setTab('login')
        setPassword('')
      }
    } catch (err: unknown) {
      const axiosErr = err as { response?: { data?: ErrorResponse } }
      const msg = axiosErr.response?.data?.error ?? 'Something went wrong'
      setError(msg)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="auth-page">
      <div className="auth-card fade-up">
        <div className="auth-logo">
          <div className="auth-logo-text">🎬 CineVault</div>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.85rem', marginTop: '0.3rem' }}>
            Sign in to unlock recommendations
          </p>
        </div>

        <div className="auth-tabs">
          <button
            id="tab-login"
            className={`auth-tab${tab === 'login' ? ' active' : ''}`}
            onClick={() => { setTab('login'); reset() }}
          >Sign In</button>
          <button
            id="tab-register"
            className={`auth-tab${tab === 'register' ? ' active' : ''}`}
            onClick={() => { setTab('register'); reset() }}
          >Register</button>
        </div>

        {error && <div className="auth-error">⚠️ {error}</div>}
        {success && <div className="auth-success">✅ {success}</div>}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label" htmlFor="auth-email">Email</label>
            <input id="auth-email" type="email" className="form-input"
              placeholder="you@example.com" value={email}
              onChange={e => setEmail(e.target.value)} autoComplete="email" />
          </div>
          <div className="form-group">
            <label className="form-label" htmlFor="auth-password">Password</label>
            <input id="auth-password" type="password" className="form-input"
              placeholder="••••••••" value={password}
              onChange={e => setPassword(e.target.value)}
              autoComplete={tab === 'login' ? 'current-password' : 'new-password'} />
          </div>
          <button id="auth-submit" type="submit"
            className="btn btn-primary btn-full" disabled={loading}>
            {loading ? 'Please wait...' : tab === 'login' ? 'Sign In' : 'Create Account'}
          </button>
        </form>

        <div style={{ textAlign: 'center', marginTop: '1.5rem' }}>
          <Link to="/" style={{ color: 'var(--text-muted)', fontSize: '0.85rem', textDecoration: 'none' }}>
            ← Back to movies
          </Link>
        </div>
      </div>
    </div>
  )
}
