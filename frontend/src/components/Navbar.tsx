import { Link, useNavigate } from 'react-router-dom'

export default function Navbar() {
  const navigate = useNavigate()
  const token = localStorage.getItem('token')

  const handleLogout = () => {
    localStorage.removeItem('token')
    navigate('/login')
  }

  return (
    <nav className="navbar">
      <Link to="/" className="navbar-brand">🎬 CineVault</Link>
      <div className="navbar-actions">
        {token ? (
          <button className="btn btn-danger" onClick={handleLogout}>
            Sign Out
          </button>
        ) : (
          <Link to="/login" className="btn btn-primary">
            Sign In
          </Link>
        )}
      </div>
    </nav>
  )
}
