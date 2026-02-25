package com.albumarchive.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.albumarchive.entity.Album;
import com.albumarchive.service.AddAlbumService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AddController {

    private final AddAlbumService addAlbumService;

    //アルバム追加画面表示
	@GetMapping("/AlbumArchive/add")
	public String showAddPage(Model model) {
		model.addAttribute("activeTab", "add");
		return "add";
	}

    //アルバム検索結果画面表示
    @GetMapping("/AlbumArchive/add/search")
    public String showSearchResultPage(@RequestParam String query, Model model) {
        List<Album> albumList = addAlbumService.searchAlbums(query);
        model.addAttribute("albums", albumList);
        model.addAttribute("activeTab", "add");
        return "add";
    }

    //アルバム登録確認画面表示
    @GetMapping("/AlbumArchive/add/confirm")
    public String showAddConfirmPage(
                        @RequestParam String artistName,
                        @RequestParam String albumName,
                        Model model
                    ) {
        List<Album> albumList = addAlbumService.searchAlbums(artistName);
        
        Album albumDetails = null;
        for (Album album : albumList) {
            if (album.getAlbumName().equals(albumName)) {
                albumDetails = album;
                break;
            }
        }
        model.addAttribute("albumDetails", albumDetails);
        model.addAttribute("activeTab", "add");
        return "add-confirm";
    }

    
}
