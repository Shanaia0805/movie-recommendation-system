package com.lexiao.assignment2.mappers;

import com.lexiao.assignment2.entities.Movie;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MovieMapper {

    @Select("""
            SELECT m.movieId, m.title, g.genre 
            FROM movies m
            INNER JOIN movies_genres mg ON m.movieId = mg.movieId
            INNER JOIN genres g ON mg.genreId = g.genreId
            WHERE m.movieId = #{movieId}
            """)
    List<Movie> findMovieWithGenresById(@Param("movieId") int movieId);


    @Select("SELECT DISTINCT movieId, title FROM movies WHERE title LIKE #{keyword} LIMIT 10")
    List<Movie> searchByTitle(@Param("keyword") String keyword);

    @Select("SELECT 1")
    Integer checkDatabaseConnection();

}
