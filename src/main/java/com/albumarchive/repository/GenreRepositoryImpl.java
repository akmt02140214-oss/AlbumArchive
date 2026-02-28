package com.albumarchive.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.albumarchive.entity.AlbumGenre;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class GenreRepositoryImpl implements GenreRepository{

    private final JdbcTemplate jdbcTemplate;
    
    @Override
    public void addGenre(AlbumGenre albumGenre) {
        String sql = "INSERT INTO album_genres (album_id, genre) VALUES (?, ?)";
        jdbcTemplate.update(sql, albumGenre.getAlbumId(), albumGenre.getGenre());
    }
}
