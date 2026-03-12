package com.albumarchive.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

// 画面表示・フォーム受け取り・API検索結果の表示に使用
@Data
public class AlbumForm {

	@NotBlank(message = "アルバム名は必須です")
	private String albumName;

	@NotBlank(message = "アーティスト名は必須です")
	private String artistName;

	private String imageUrl;

	private List<String> genres;

	@NotNull(message = "評価を選択してください")
	@Min(value = 1, message = "評価は1以上にしてください")
	@Max(value = 5, message = "評価は5以下にしてください")
	private Integer rating;

	@Size(max = 500, message = "メモは500文字以内で入力してください")
	private String memo;

	private LocalDateTime registerDate;

}
