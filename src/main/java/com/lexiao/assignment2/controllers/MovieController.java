package com.lexiao.assignment2.controllers;

import com.lexiao.assignment2.entities.Link;
import com.lexiao.assignment2.entities.Movie;
import com.lexiao.assignment2.services.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/v1")
public class MovieController {

    @Autowired
    private MovieService movieService;

    @GetMapping("/movies/search")
    public ResponseEntity<?> searchMovies(@RequestParam("q") String q) {
        if (q == null || q.trim().length() < 2) {
            return ResponseEntity.badRequest().body(Map.of("error", "Query must be at least 2 characters"));
        }
        List<Movie> results = movieService.searchMoviesByTitle(q.trim());
        return ResponseEntity.ok(results);
    }

    @GetMapping("/movie")

    public Object getMovieByQueryParam(@RequestParam("id") int movieId) {
        return getMovie(movieId);
    }

    @GetMapping("/movie/{movieId}")
    public Object getMovieByPath(@PathVariable int movieId) {
        return getMovie(movieId);
    }

    private ResponseEntity<?> getMovie(int movieId) {
        List<Movie> movies = movieService.getMovieById(movieId);
        if (movies == null || movies.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Movie not found"));
        }
        Map<String, Object> result = new HashMap<>();
        result.put("movieId", movies.get(0).getMovieId());
        result.put("title", movies.get(0).getTitle());
        List<String> genres = new ArrayList<>();
        for (Movie movie : movies) {
            genres.add(movie.getGenre());
        }
        result.put("genres", genres);

        return ResponseEntity.status((HttpStatus.OK)).body(result);
    }


    @GetMapping("/rating/{movieId}")
    public Object getAverageRating(@PathVariable int movieId) {
        Double avgRating = movieService.getAverageRating(movieId);
        if (avgRating == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "No ratings found for this movie"));
        }
        Map<String, Object> result = new HashMap<>();
        result.put("movieId", movieId);
        result.put("average_rating", avgRating);
        return ResponseEntity.status((HttpStatus.OK)).body(result);
    }

    @GetMapping("/link/{movieId}")
    public Object getLink(@PathVariable int movieId) {
        Link link = movieService.getLinkByMovieId(movieId);
        if (link == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Link not found for this movie"));
        }
        return ResponseEntity.status((HttpStatus.OK)).body(link);
    }
}