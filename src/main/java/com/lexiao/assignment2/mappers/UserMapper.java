package com.lexiao.assignment2.mappers;
import com.lexiao.assignment2.entities.User;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;


@Mapper
public interface UserMapper {
    @Insert("INSERT INTO users(email, password) VALUES(#{email}, #{password})")
    int insertUser(User user);

    @Select("SELECT * FROM users WHERE email = #{email}")
    User findByEmail(String email);
}