package com.albumarchive.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.albumarchive.entity.Album;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AlbumRepositoryImpl implements AlbumRepository {
	
	private final JdbcTemplate jdbcTemplate;

	@Override
	public List<Album> findRecentAlbum(int limit) {
	    String sql = "SELECT * FROM albums ORDER BY register_date DESC LIMIT ?";

	    return jdbcTemplate.query(sql, new RowMapper<Album>() {
	        @Override
	        public Album mapRow(ResultSet rs, int rowNum) throws SQLException {
	            Album album = new Album();
	            // カラム名を直接指定してセットする
	            album.setAlbumName(rs.getString("album_name"));
	            album.setArtistName(rs.getString("artist_name"));
	            album.setArtworkUrl(rs.getString("artwork_url")); // ここをDBのカラム名と完全に一致させる
	            
	            if (rs.getTimestamp("register_date") != null) {
	                album.setRegisterDate(rs.getTimestamp("register_date").toLocalDateTime());
	            }
	            return album;
	        }
	    }, limit);
	}
	@Override
	public void albumRegister(Album album) {
		// TODO 自動生成されたメソッド・スタブ

	}

}
