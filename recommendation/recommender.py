"""
Movie Recommendation Service
Content-Based Filtering using Genre Cosine Similarity
"""

import mysql.connector
import numpy as np
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity

import os

DB_CONFIG = {
    "host": os.getenv("DB_HOST", "mysql"),
    "user": os.getenv("DB_USERNAME", "root"),
    "password": os.getenv("DB_PASSWORD", "CineVault2024!"),
    "database": os.getenv("DB_NAME", "movie_db"),
}

# Global state — built once on startup
_movie_ids = []
_movie_map = {}      # movieId -> {title, genres}
_similarity = None   # cosine similarity matrix


def build_model():
    """Load all movies from MySQL and build the genre similarity matrix."""
    global _movie_ids, _movie_map, _similarity

    try:
        conn = mysql.connector.connect(**DB_CONFIG)
        cursor = conn.cursor(dictionary=True)

        cursor.execute("""
            SELECT m.movieId, m.title, GROUP_CONCAT(g.genre ORDER BY g.genre SEPARATOR '|') AS genres
            FROM movies m
            JOIN movies_genres mg ON m.movieId = mg.movieId
            JOIN genres g ON mg.genreId = g.genreId
            GROUP BY m.movieId, m.title
        """)
        rows = cursor.fetchall()
        cursor.close()
        conn.close()
    except Exception as e:
        print(f"⚠️ Could not build model: {e}")
        print("💡 The local database might be empty or tables are missing.")
        rows = []

    _movie_ids = [r["movieId"] for r in rows]
    _movie_map = {
        r["movieId"]: {
            "movieId": r["movieId"],
            "title": r["title"],
            "genres": r["genres"].split("|") if r["genres"] else []
        }
        for r in rows
    }

    # TF-IDF on genres string (e.g. "Action|Comedy|Drama")
    corpus = [r["genres"] or "(no genres listed)" for r in rows]
    vectorizer = TfidfVectorizer(token_pattern=r"[^|]+")
    tfidf_matrix = vectorizer.fit_transform(corpus)

    _similarity = cosine_similarity(tfidf_matrix, tfidf_matrix)
    print(f"✅ Recommendation model built: {len(_movie_ids)} movies indexed.")


def get_recommendations(movie_id: int, top_n: int = 10) -> list:
    """Return top_n most similar movies to the given movie_id."""
    if movie_id not in _movie_map:
        return []

    idx = _movie_ids.index(movie_id)
    sim_scores = list(enumerate(_similarity[idx]))
    # Sort by similarity descending, skip self (index 0 is itself)
    sim_scores.sort(key=lambda x: x[1], reverse=True)
    sim_scores = [s for s in sim_scores if _movie_ids[s[0]] != movie_id]

    results = []
    for i, score in sim_scores[:top_n]:
        mid = _movie_ids[i]
        movie = _movie_map[mid].copy()
        movie["similarity"] = round(float(score), 4)
        results.append(movie)

    return results
