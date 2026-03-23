package com.albumarchive.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.albumarchive.entity.Album;
import com.albumarchive.entity.AlbumForm;
import com.albumarchive.service.AlbumService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class LibraryController {

	private final AlbumService albumService;

	// アクティブタブのメソッド(共通)
	@ModelAttribute("activeTab")
	public String activeTab() {
		return "library";
	}

	// ライブラリ画面表示
	@GetMapping("/AlbumArchive/library")
	public String showLibraryPage(@RequestParam(defaultValue = "0") int pageCount,
			@RequestParam(defaultValue = "newest") String sort,
			Model model) {

		int pageSize = 30;

		int totalAlbums = albumService.getTotalAlbumCount();

		int totalPages = (int) Math.ceil((double) totalAlbums / pageSize);
		if (totalPages == 0)
			totalPages = 1;

		List<Album> myAlbums = albumService.searchMyAlbums(pageCount * pageSize, sort);

		model.addAttribute("myAlbums", myAlbums);
		model.addAttribute("pageCount", pageCount);
		model.addAttribute("totalPages", totalPages);
		model.addAttribute("sort", sort);
		return "library";
	}

	// 登録済みアルバム詳細情報取得処理
	@GetMapping("/AlbumArchive/library/details")
	public String showLibraryDetailsPage(@RequestParam("id") Long id, Model model) {

		Album album = albumService.getAlbumById(id);

		List<String> genres = albumService.getGenresByAlbumId(id);

		model.addAttribute("album", album);
		model.addAttribute("genres", genres);
		return "library-details";
	}

	// 登録済みアルバム編集処理
	@PostMapping("/AlbumArchive/library/update")
	public String showLibraryPageRedirect(@RequestParam("id") Long id, AlbumForm albumForm, RedirectAttributes ra) {

		albumService.updateAlbum(id, albumForm);

		ra.addFlashAttribute("message", "Album updated.");
		return "redirect:/AlbumArchive/library/details?id=" + id;
	}

	// 登録済みアルバム削除処理
	@PostMapping("/AlbumArchive/library/delete")
	public String deleteAlbum(@RequestParam("id") Long id, RedirectAttributes ra) {

		albumService.deleteAlbum(id);
		ra.addFlashAttribute("message", "Album deleted.");

		return "redirect:/AlbumArchive/library";
	}
}
