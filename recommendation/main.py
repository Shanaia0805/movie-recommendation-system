"""
FastAPI entry point for the Movie Recommendation Service.
Runs on port 8000.
"""

from contextlib import asynccontextmanager
from fastapi import FastAPI, HTTPException, Query
from fastapi.middleware.cors import CORSMiddleware
from recommender import build_model, get_recommendations


@asynccontextmanager
async def lifespan(app: FastAPI):
    """Build the recommendation model once on startup."""
    print("🚀 Building recommendation model (this may take ~30s)...")
    build_model()
    yield


app = FastAPI(
    title="CineVault Recommendation API",
    description="Content-Based Movie Recommendations using Genre Similarity",
    version="1.0.0",
    lifespan=lifespan,
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:3000"],
    allow_methods=["GET"],
    allow_headers=["*"],
)


@app.get("/recommend/{movie_id}")
def recommend(
    movie_id: int,
    top: int = Query(default=10, ge=1, le=30),
):
    """
    Get top-N content-based recommendations for a movie.
    Returns movies with similar genres, sorted by cosine similarity score.
    """
    results = get_recommendations(movie_id, top_n=top)
    if not results and movie_id < 1:
        raise HTTPException(status_code=400, detail="Invalid movie_id")
    return results


@app.get("/health")
def health():
    return {"status": "ok", "service": "recommendation"}
