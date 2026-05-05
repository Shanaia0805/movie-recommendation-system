// Shared TypeScript types for CineVault

export interface Movie {
  movieId: number
  title: string
  genres?: string[]
  genre?: string   // raw field from backend before aggregation
}

export interface Rating {
  average_rating: number
}

export interface Link {
  movieId: number
  imdbId?: string
  tmdbId?: string
}

export interface Recommendation extends Movie {
  similarity?: number
  tmdbId?: string
}

export interface AuthResponse {
  token: string
}

export interface ErrorResponse {
  error: string
}
