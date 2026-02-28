package com.albumarchive.entity;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;


// 画面表示・フォーム受け取り・API検索結果の表示に使用
@Data
public class AlbumForm {
	
	private String albumName;
	private String artistName;
	private String imageUrl;
	private List<String> genres;
	private Integer rating;
	private String memo;
	private LocalDateTime registerDate;
	
}
