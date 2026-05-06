package com.lexiao.assignment2.entities;

import lombok.Data;

import java.io.Serializable;

@Data
public class Movie implements Serializable {
    private static final long serialVersionUID = 1L;
    private int movieId;
    private String title;
    private String genre;
}