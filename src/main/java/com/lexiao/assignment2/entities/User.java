package com.lexiao.assignment2.entities;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {
    private Integer id;
    private String email;
    private String password;
    private LocalDateTime createdAt;
}