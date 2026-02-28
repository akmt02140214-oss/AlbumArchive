package com.albumarchive.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.albumarchive.entity.AlbumForm;
import com.albumarchive.service.AlbumService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AddController {

    private final AlbumService albumService;

    //アルバム追加画面表示
	@GetMapping("/AlbumArchive/add")
	public String showAddPage(Model model) {
		model.addAttribute("activeTab", "add");
		return "add";
	}

    //アルバム検索結果画面表示
    @GetMapping("/AlbumArchive/add/search")
    public String showSearchResultPage(@RequestParam String query, Model model) {
        List<AlbumForm> albumList = albumService.searchAlbums(query);
        model.addAttribute("albums", albumList);
        model.addAttribute("searchQuery", query);
        model.addAttribute("activeTab", "add");
        return "add";
    }

    //アルバム登録確認画面表示
    @GetMapping("/AlbumArchive/add/confirm")
    public String showAddConfirmPage(
                        @RequestParam String artistName,
                        @RequestParam String albumName,
                        @RequestParam String query,
                        Model model
                    ) {
        List<AlbumForm> albumList = albumService.searchAlbums(artistName);
        
        AlbumForm albumDetails = null;
        for (AlbumForm album : albumList) {
            if (album.getAlbumName().equals(albumName)) {
                albumDetails = album;
                break;
            }
        }
        model.addAttribute("albumDetails", albumDetails);
        model.addAttribute("searchQuery", query);
        model.addAttribute("activeTab", "add");
        return "add-confirm";
    }

    // アルバム登録完了画面表示(add.htmlへのリダイレクト)
    @PostMapping("/AlbumArchive/add/register")
    public String showAddPageRedirect(RedirectAttributes ra, AlbumForm albumForm) {
        albumService.addAlbum(albumForm);
        ra.addFlashAttribute("successMsg", "Added to your library.");
        return "redirect:/AlbumArchive/add";
    }

    
}
