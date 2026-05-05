const TMDB_KEY = import.meta.env.VITE_TMDB_API_KEY as string
const BASE_URL = 'https://api.themoviedb.org/3/movie'
const IMG_BASE = 'https://image.tmdb.org/t/p/w500'

const cache = new Map<string, string | null>()

export async function fetchPoster(tmdbId: string | number): Promise<string | null> {
  if (!tmdbId || !TMDB_KEY) return null
  const key = String(tmdbId)
  if (cache.has(key)) return cache.get(key)!

  try {
    const res = await fetch(`${BASE_URL}/${key}?api_key=${TMDB_KEY}`)
    if (!res.ok) return null
    const data = await res.json()
    const url: string | null = data.poster_path ? `${IMG_BASE}${data.poster_path}` : null
    cache.set(key, url)
    return url
  } catch {
    return null
  }
}
