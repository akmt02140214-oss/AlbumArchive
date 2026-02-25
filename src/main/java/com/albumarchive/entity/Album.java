package com.albumarchive.entity;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class Album {
	
	private String albumName;
	private String artistName;
	private String imageUrl;
	private String releaseDate;
	private LocalDateTime registerDate;
	private int trackCount;
	private List<String> genres;
}
