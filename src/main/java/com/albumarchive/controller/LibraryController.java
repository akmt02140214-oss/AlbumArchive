package com.albumarchive.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.albumarchive.entity.Album;
import com.albumarchive.service.AlbumService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class LibraryController {

	private final AlbumService albumService;
    
    	//ライブラリ画面表示
	@GetMapping("/AlbumArchive/library")
	public String showLibraryPage(@RequestParam(defaultValue = "0") int pageCount,
	                          @RequestParam(defaultValue = "newest") String sort,
							   Model model){

		int pageSize = 30;

		int totalAlbums = albumService.getTotalAlbumCount();

		int totalPages = (int) Math.ceil((double) totalAlbums / pageSize);
		if (totalPages == 0) totalPages = 1;

		
        List<Album> myAlbums = albumService.searchMyAlbums(pageCount * pageSize, sort);

		model.addAttribute("myAlbums", myAlbums);
		model.addAttribute("pageCount", pageCount);
		model.addAttribute("totalPages", totalPages);
		model.addAttribute("sort", sort);
		model.addAttribute("activeTab", "library");
		return "library";
	}

	@GetMapping("/AlbumArchive/library/details")
	public String showLibraryDetailsPage(@RequestParam("id") Long id, Model model) {

		Album album = albumService.getAlbumById(id);

		List<String> genres = albumService.getGenresByAlbumId(id);

		model.addAttribute("album", album);
		model.addAttribute("genres", genres);
		model.addAttribute("activeTab", "library");
		return "library-details";
	}
}
