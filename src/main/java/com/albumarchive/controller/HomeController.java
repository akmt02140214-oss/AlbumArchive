package com.albumarchive.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.albumarchive.entity.Album;
import com.albumarchive.repository.AlbumRepository;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class HomeController {
	
	private final AlbumRepository albumRepository;
	
	//ホーム画面表示
	@GetMapping("/AlbumArchive")
	public String showHome(Model model) {
		List<Album> recentAlbums = albumRepository.findRecentAlbum(5);
		
		model.addAttribute("activeTab", "home");
		model.addAttribute("albums", recentAlbums);
		return "home";
	}
	
	//ライブラリ画面表示
	@GetMapping("/AlbumArchive/library")
	public String showLibrary(Model model){
		model.addAttribute("activeTab", "library");
		return "library";
	}
}
