package com.albumarchive.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
	public String showLibrary(@RequestParam(defaultValue = "0") int pageCount, Model model){

		int pageSize = 30;

		int totalAlbums = albumService.getTotalAlbumCount();

		int totalPages = (int) Math.ceil((double) totalAlbums / pageSize);
		if (totalPages == 0) totalPages = 1;

		
        List<Album> myAlbums = albumService.searchMyAlbums(pageCount * pageSize);

		model.addAttribute("myAlbums", myAlbums);
		model.addAttribute("pageCount", pageCount);
		model.addAttribute("totalPages", totalPages);
		model.addAttribute("activeTab", "library");
		return "library";
	}
}
