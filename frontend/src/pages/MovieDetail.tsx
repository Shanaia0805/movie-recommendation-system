import { useState, useEffect } from 'react'
import { useParams, Link, useNavigate } from 'react-router-dom'
import Navbar from '../components/Navbar'
import api from '../api'
import { fetchPoster } from '../tmdb'
import type { Movie, Rating, Link as LinkType, Recommendation } from '../types'

const GENRE_EMOJIS: Record<string, string> = {
  Action: '💥', Comedy: '😂', Drama: '🎭', Horror: '👻',
  Romance: '💕', 'Sci-Fi': '🚀', Thriller: '🔪', Animation: '🎨',
  Adventure: '🗺️', Fantasy: '✨', Crime: '🔍', Documentary: '🎞️',
  Children: '🧒', Musical: '🎵', War: '⚔️', Western: '🤠',
}

interface StarRatingProps {
  rating: number
}

function StarRating({ rating }: StarRatingProps) {
  const stars = Math.round((rating / 5) * 5)
  return (
    <span style={{ color: 'var(--gold-light)', letterSpacing: 2 }}>
      {'★'.repeat(stars)}{'☆'.repeat(5 - stars)}
    </span>
  )
}

interface RecommendCardProps {
  movie: Recommendation
  onClick: () => void
}

function RecommendCard({ movie, onClick }: RecommendCardProps) {
  const [posterUrl, setPosterUrl] = useState<string | null>(null)
  const firstGenre = movie.genres?.[0] ?? 'Movie'
  const emoji = GENRE_EMOJIS[firstGenre] ?? '🎬'

  useEffect(() => {
    if (movie.tmdbId) fetchPoster(movie.tmdbId).then(setPosterUrl)
  }, [movie.tmdbId])

  return (
    <div className="movie-card" onClick={onClick} style={{ cursor: 'pointer' }}>
      <div className="movie-card-poster" style={{ aspectRatio: '2/3', fontSize: '2.5rem' }}>
        {posterUrl
          ? <img src={posterUrl} alt={movie.title} style={{ width: '100%', height: '100%', objectFit: 'cover' }} />
          : <span>{emoji}</span>
        }
      </div>
      <div className="movie-card-body">
        <div className="movie-card-title" style={{ fontSize: '0.85rem' }}>{movie.title}</div>
        <div className="movie-card-meta">
          {movie.genres?.slice(0, 1).map(g => <span key={g} className="genre-tag">{g}</span>)}
        </div>
        {movie.similarity !== undefined && (
          <div style={{ marginTop: '0.4rem', fontSize: '0.75rem', color: 'var(--text-muted)' }}>
            {Math.round(movie.similarity * 100)}% match
          </div>
        )}
      </div>
    </div>
  )
}

export default function MovieDetail() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const [movie, setMovie] = useState<Movie | null>(null)
  const [rating, setRating] = useState<number | null>(null)
  const [link, setLink] = useState<LinkType | null>(null)
  const [posterUrl, setPosterUrl] = useState<string | null>(null)
  const [recommendations, setRecommendations] = useState<Recommendation[]>([])
  const [loading, setLoading] = useState<boolean>(true)
  const [recLoading, setRecLoading] = useState<boolean>(true)
  const [error, setError] = useState<string>('')

  useEffect(() => {
    setLoading(true); setError(''); setMovie(null)
    setRating(null); setLink(null); setPosterUrl(null)
    setRecommendations([]); setRecLoading(true)

    const token = localStorage.getItem('token')
    if (!token) { navigate('/login'); return }

    Promise.all([
      api.get<Movie>(`/v1/movie/${id}`),
      api.get<Rating>(`/v1/rating/${id}`).catch(() => null),
      api.get<LinkType>(`/v1/link/${id}`).catch(() => null),
    ]).then(([movieRes, ratingRes, linkRes]) => {
      setMovie(movieRes.data)
      if (ratingRes) setRating(ratingRes.data.average_rating)
      if (linkRes) {
        setLink(linkRes.data)
        if (linkRes.data?.tmdbId) fetchPoster(linkRes.data.tmdbId).then(setPosterUrl)
      }
      setLoading(false)
    }).catch(() => { setError('Movie not found.'); setLoading(false) })

    api.get<Recommendation[]>(`/recommend/${id}?top=8`)
      .then(r => setRecommendations(r.data ?? []))
      .catch(() => setRecommendations([]))
      .finally(() => setRecLoading(false))
  }, [id, navigate])

  if (loading) return (<><Navbar /><div className="detail-page"><div className="spinner" /></div></>)

  if (error) return (
    <><Navbar />
      <div className="detail-page">
        <Link to="/" className="detail-back">← Back</Link>
        <div className="empty-state"><div className="empty-state-icon">🎬</div><p>{error}</p></div>
      </div>
    </>
  )

  const firstGenre = movie?.genres?.[0] ?? 'Movie'
  const emoji = GENRE_EMOJIS[firstGenre] ?? '🎬'

  return (
    <>
      <Navbar />
      <div className="detail-page fade-up">
        <Link to="/" className="detail-back">← Back to Home</Link>

        <div className="detail-hero">
          <div className="detail-poster">
            {posterUrl
              ? <img src={posterUrl} alt={movie?.title} style={{ width: '100%', height: '100%', objectFit: 'cover', borderRadius: 'var(--radius-md)' }} />
              : <span>{emoji}</span>
            }
          </div>

          <div className="detail-info">
            <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Movie ID: {movie?.movieId}</div>
            <h1 className="detail-title">{movie?.title}</h1>

            <div className="detail-genres">
              {movie?.genres?.map(g => <span key={g} className="genre-tag">{g}</span>)}
            </div>

            {rating !== null && (
              <div className="detail-stat">
                <div>
                  <div className="detail-stat-label">Average Rating</div>
                  <div className="detail-stat-value" style={{ color: 'var(--gold-light)' }}>
                    ⭐ {Number(rating).toFixed(2)} / 5.00
                  </div>
                  <StarRating rating={rating} />
                </div>
              </div>
            )}

            <div className="detail-links">
              {link?.imdbId && (
                <a href={`https://www.imdb.com/title/tt${link.imdbId}`}
                  target="_blank" rel="noopener noreferrer"
                  className="external-link" id="imdb-link">
                  🎞️ View on IMDb
                </a>
              )}
              {link?.tmdbId && (
                <a href={`https://www.themoviedb.org/movie/${link.tmdbId}`}
                  target="_blank" rel="noopener noreferrer"
                  className="external-link" id="tmdb-link">
                  🎬 View on TMDB
                </a>
              )}
            </div>
          </div>
        </div>

        <div>
          <div className="section-title">✨ You Might Also Like</div>
          {recLoading ? (
            <div className="spinner" />
          ) : recommendations.length > 0 ? (
            <div className="recommend-grid">
              {recommendations.map(rec => (
                <RecommendCard key={rec.movieId} movie={rec}
                  onClick={() => navigate(`/movie/${rec.movieId}`)} />
              ))}
            </div>
          ) : (
            <div className="empty-state" style={{ padding: '2rem' }}>
              <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>
                Recommendation service starting up...
              </p>
            </div>
          )}
        </div>
      </div>
    </>
  )
}
