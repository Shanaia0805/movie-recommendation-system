package com.lexiao.assignment2.mappers;



import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;


@Mapper
public interface RatingMapper {
    @Select("SELECT AVG(rating) FROM ratings WHERE movieId = #{movieId}")
    Double findAverageRating(@Param("movieId") int movieId);
}
