package com.lexiao.assignment2.mappers;

import com.lexiao.assignment2.entities.Link;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface LinkMapper {

    @Select("SELECT movieId, imdbId, tmdbId FROM links WHERE movieId = #{movieId}")
    Link findLinkByMovieId(@Param("movieId") int movieId);
}
