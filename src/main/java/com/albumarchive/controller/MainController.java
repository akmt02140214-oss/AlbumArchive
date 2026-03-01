package com.albumarchive.controller;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

	//ホーム画面表示
	@GetMapping("/AlbumArchive")
	public String showHome(Model model) {
		model.addAttribute("activeTab", "home");		
		return "home";
	}

	//アルバム編集画面表示
	@GetMapping("/AlbumArchive/edit")
	public String showEdit(Model model) {
		model.addAttribute("activeTab", "edit");
		return "edit";
	}

}
