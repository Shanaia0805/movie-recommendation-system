import json
import os
import requests

TMDB_API_KEY = os.environ.get("TMDB_API_KEY")

def lambda_handler(event, context):
    headers = event.get("headers", {})
    content_type = headers.get("Content-Type", "application/json")

    path_params = event.get("pathParameters") or {}
    movie_id = path_params.get("movie_id")

    # === Input validation ===
    if not movie_id:
        return _response(400, {"error": "movie id is required"}, content_type)

    if not movie_id.isdigit():
        return _response(400, {"error": "invalid movie id"}, content_type)

    movie_id_int = int(movie_id)
    if movie_id_int < 1 or movie_id_int > 999999:
        return _response(400, {"error": "movie id is out of range"}, content_type)

    # === TMDB Integration ===
    tmdb_url = f"https://api.themoviedb.org/3/movie/{movie_id}?api_key={TMDB_API_KEY}"
    tmdb_response = requests.get(tmdb_url)

    if tmdb_response.status_code != 200:
        return _response(404, {"error": "movie not found"}, content_type)

    tmdb_data = tmdb_response.json()
    poster_path = tmdb_data.get("poster_path")

    if not poster_path:
        return _response(404, {"error": "poster not found"}, content_type)

    poster_url = f"https://image.tmdb.org/t/p/w500{poster_path}"

    response_body = {
        "movie_id": movie_id_int,
        "poster_url": poster_url,
        "poster_size": 1024000,
        "message": "poster details fetched successfully"
    }

    return _response(200, response_body, content_type)


def _response(status_code, body, content_type):
    if content_type == "text/plain":
        plain_text = "\n".join(f"{k}: {v}" for k, v in body.items())
        return {
            "statusCode": status_code,
            "headers": {"Content-Type": "text/plain"},
            "body": plain_text
        }
    else:
        return {
            "statusCode": status_code,
            "headers": {"Content-Type": "application/json"},
            "body": json.dumps(body)
        }
