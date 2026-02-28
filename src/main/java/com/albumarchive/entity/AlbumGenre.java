package com.albumarchive.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;

// アルバムジャンル用Entity
@Data
@Table("album_genres")
public class AlbumGenre {
	
	@Id
	private Long id;
	private Long albumId;
	private String genre;
	
}
