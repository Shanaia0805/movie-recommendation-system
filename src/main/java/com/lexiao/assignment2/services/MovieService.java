package com.lexiao.assignment2.services;

import com.lexiao.assignment2.entities.Link;
import com.lexiao.assignment2.entities.Movie;
import com.lexiao.assignment2.mappers.MovieMapper;
import com.lexiao.assignment2.mappers.RatingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class MovieService {

    private final MovieMapper movieMapper;
    private final RatingMapper ratingMapper;
    private final com.lexiao.assignment2.mappers.LinkMapper linkMapper;

    @Autowired
    public MovieService(MovieMapper movieMapper, RatingMapper ratingMapper, com.lexiao.assignment2.mappers.LinkMapper linkMapper) {
        this.movieMapper = movieMapper;
        this.ratingMapper = ratingMapper;
        this.linkMapper = linkMapper;
    }

    // Keeping the same names you used in your earlier files to avoid any conflicts
    @Cacheable(value = "movies", key = "#movieId")
    public List<Movie> getMovieById(int movieId) {
        List<Movie> movies = movieMapper.findMovieWithGenresById(movieId);
        for (Movie movie : movies) {
            movie.setGenre(movie.getGenre().replace("\r", "").trim());
        }
        return movies;
    }

    public List<Movie> searchMoviesByTitle(String keyword) {
        return movieMapper.searchByTitle("%" + keyword + "%");
    }


    @Cacheable(value = "ratings", key = "#movieId")
    public Double getAverageRating(int movieId) {
        return ratingMapper.findAverageRating(movieId);  // Match with RatingMapper method
    }

    public Link getLinkByMovieId(int movieId) {
        Link link = linkMapper.findLinkByMovieId(movieId);  // Match with LinkMapper method
        if (link != null) {
            // Clean the tmdbId to ensure no unwanted characters
            link.setTmdbId(link.getTmdbId().replace("\r", "").trim());
        }
        return link;
    }

    public boolean isDatabaseConnected() {
        try {
            Integer result = movieMapper.checkDatabaseConnection();  // Match with MovieMapper method
            return (result != null && result == 1);
        } catch (Exception e) {
            // If an exception occurs, it indicates that the database is unavailable.
            return false;
        }
    }
}
