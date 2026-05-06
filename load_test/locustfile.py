from locust import HttpUser, task, between
import random

class CineVaultUser(HttpUser):
    # Simulate user thinking time between 1 and 3 seconds
    wait_time = between(1, 3)

    @task(3)
    def browse_movie(self):
        """Simulate browsing a movie (mostly READ operations)"""
        # We know we have movie IDs like 1, 2, 296, 318, 356, 527, 593, 858, 2571, 79132 in the DB
        movie_ids = [1, 2, 296, 318, 356, 527, 593, 858, 2571, 79132]
        movie_id = random.choice(movie_ids)
        self.client.get(f"/v1/movie/{movie_id}", name="/v1/movie/[id]")

    @task(1)
    def rate_movie(self):
        """Simulate rating a movie (WRITE operation sent to Kafka)"""
        movie_ids = [1, 2, 296, 318, 356, 527, 593, 858, 2571, 79132]
        movie_id = random.choice(movie_ids)
        user_id = random.randint(1, 1000)
        rating = round(random.uniform(0.5, 5.0), 1)
        
        payload = {
            "userId": user_id,
            "movieId": movie_id,
            "rating": rating
        }
        self.client.post("/v1/rating", json=payload, name="/v1/rating")
