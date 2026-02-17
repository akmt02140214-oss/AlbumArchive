package com.albumarchive.entity;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Album {
	
	private String albumName;
	private String artistName;
	private String artworkUrl;
	private LocalDateTime registerDate;
	

}
