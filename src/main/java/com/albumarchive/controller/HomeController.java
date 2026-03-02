package com.albumarchive.controller;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.albumarchive.entity.Album;
import com.albumarchive.service.AlbumService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class HomeController {

	private final AlbumService albumService;

	//ホーム画面表示
	@GetMapping("/AlbumArchive")
	public String showHome(Model model) {
		
		List<Album> recentAlbums = albumService.getRecentAlbums();
		
		model.addAttribute("myAlbums", recentAlbums);
		model.addAttribute("activeTab", "home");		
		return "home";
	}
}
