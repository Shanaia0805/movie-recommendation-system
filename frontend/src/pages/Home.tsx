import { useState, useCallback } from 'react'
import { useNavigate } from 'react-router-dom'
import Navbar from '../components/Navbar'
import api from '../api'
import type { Movie } from '../types'

const POPULAR_IDS: number[] = [1, 2, 3, 356, 318, 296, 593, 527, 858, 260, 1196, 480]

const GENRE_EMOJIS: Record<string, string> = {
  Action:'💥', Comedy:'😂', Drama:'🎭', Horror:'👻', Romance:'💕',
  'Sci-Fi':'🚀', Thriller:'🔪', Animation:'🎨', Adventure:'🗺️',
  Fantasy:'✨', Crime:'🔍', Documentary:'🎞️', Children:'🧒',
  Musical:'🎵', War:'⚔️', Western:'🤠', Mystery:'🕵️',
}

interface MovieCardProps {
  id: number
}

function MovieCard({ id }: MovieCardProps) {
  const [movie, setMovie] = useState<Movie | null>(null)
  const [loaded, setLoaded] = useState(false)
  const navigate = useNavigate()

  useState(() => {
    api.get<Movie>(`/v1/movie/${id}`)
      .then(r => { setMovie(r.data); setLoaded(true) })
      .catch(() => setLoaded(true))
  })

  if (!loaded) return (
    <div className="movie-card" style={{ minHeight: 280 }}>
      <div className="movie-card-poster">
        <div className="spinner" style={{ width: 24, height: 24, margin: 0 }} />
      </div>
      <div className="movie-card-body">
        <div style={{ height: 14, background: 'var(--border)', borderRadius: 4, marginBottom: 8 }} />
        <div style={{ height: 10, background: 'var(--border)', borderRadius: 4, width: '60%' }} />
      </div>
    </div>
  )

  if (!movie) return null
  const firstGenre = movie.genres?.[0] ?? 'Movie'
  const emoji = GENRE_EMOJIS[firstGenre] ?? '🎬'

  return (
    <div className="movie-card fade-up" onClick={() => navigate(`/movie/${id}`)}>
      <div className="movie-card-poster"><span>{emoji}</span></div>
      <div className="movie-card-body">
        <div className="movie-card-title">{movie.title}</div>
        <div className="movie-card-meta">
          {movie.genres?.slice(0, 2).map(g => <span key={g} className="genre-tag">{g}</span>)}
        </div>
      </div>
    </div>
  )
}

export default function Home() {
  const [query, setQuery] = useState<string>('')
  const [searchResults, setSearchResults] = useState<Movie[]>([])
  const [searching, setSearching] = useState<boolean>(false)
  const [searchError, setSearchError] = useState<string>('')
  const [hasSearched, setHasSearched] = useState<boolean>(false)
  const navigate = useNavigate()

  const handleSearch = useCallback(async (e: React.FormEvent) => {
    e.preventDefault()
    const q = query.trim()
    if (q.length < 2) { setSearchError('Please enter at least 2 characters'); return }
    setSearching(true)
    setSearchError('')
    setSearchResults([])
    setHasSearched(true)
    try {
      const res = await api.get<Movie[]>(`/v1/movies/search?q=${encodeURIComponent(q)}`)
      setSearchResults(res.data ?? [])
      if (!res.data || res.data.length === 0) setSearchError(`No movies found for "${q}"`)
    } catch {
      setSearchError('Search failed. Make sure you are logged in.')
    } finally {
      setSearching(false)
    }
  }, [query])

  return (
    <>
      <Navbar />
      <div className="hero">
        <div className="hero-badge">🎬 MovieLens 32M · 87,585 Movies</div>
        <h1>Discover Your Next<br />Favorite Movie</h1>
        <p>Search by movie title or ID, explore details, ratings, and get personalized recommendations.</p>

        <form className="search-bar" onSubmit={handleSearch}>
          <input
            id="movie-search-input"
            type="text"
            placeholder='Search by title (e.g. "Toy Story") or enter an ID...'
            value={query}
            onChange={e => { setQuery(e.target.value); setSearchError('') }}
          />
          <button type="submit" className="btn btn-primary" id="search-btn" disabled={searching}>
            {searching ? '...' : '🔍 Search'}
          </button>
        </form>

        {searchError && (
          <p style={{ color: 'var(--gold-light)', marginTop: '0.8rem', fontSize: '0.9rem' }}>
            ⚠️ {searchError}
          </p>
        )}

        {hasSearched && searchResults.length > 0 && (
          <div style={{ maxWidth: 600, margin: '1.5rem auto 0', textAlign: 'left' }}>
            <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)', marginBottom: '0.6rem', paddingLeft: '0.2rem' }}>
              {searchResults.length} result{searchResults.length > 1 ? 's' : ''} for "{query}"
            </div>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
              {searchResults.map(movie => (
                <div
                  key={movie.movieId}
                  className="movie-card fade-up"
                  onClick={() => navigate(`/movie/${movie.movieId}`)}
                  style={{ display: 'flex', alignItems: 'center', gap: '1rem', padding: '0.8rem 1rem' }}
                >
                  <span style={{ fontSize: '1.5rem' }}>🎬</span>
                  <div style={{ flex: 1 }}>
                    <div style={{ fontWeight: 600, fontSize: '0.95rem' }}>{movie.title}</div>
                    <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>ID: {movie.movieId}</div>
                  </div>
                  <span style={{ fontSize: '0.8rem', color: 'var(--accent-light)' }}>View →</span>
                </div>
              ))}
            </div>
          </div>
        )}
      </div>

      {!hasSearched && (
        <div className="section">
          <div className="section-title">🔥 Featured Movies</div>
          <div className="movies-grid">
            {POPULAR_IDS.map(id => <MovieCard key={id} id={id} />)}
          </div>
        </div>
      )}
    </>
  )
}
