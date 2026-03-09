package com.albumarchive.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.albumarchive.entity.AlbumForm;
import com.albumarchive.service.AlbumService;

@WebMvcTest(AddController.class)
public class AddControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AlbumService albumService;

    @Test
    void testShowAddConfirmPage_アルバム追加確認画面表示() throws Exception {

        // Setup
        AlbumForm album = new AlbumForm();
        album.setAlbumName("Kid A");
        album.setArtistName("Radiohead");

        when(albumService.searchAlbums("Radiohead")).thenReturn(List.of(album));

        // Exercise / Verify
        mockMvc.perform(get("/AlbumArchive/add/confirm")
                .param("artistName", "Radiohead")
                .param("albumName", "Kid A")
                .param("query", "Radiohead"))
                .andExpect(status().isOk())
                .andExpect(view().name("add-confirm"))
                .andExpect(model().attribute("albumDetails", album))
                .andExpect(model().attribute("searchQuery", "Radiohead"))
                .andExpect(model().attribute("activeTab", "add"));
    }

    @Test
    void testShowAddConfirmPage_複数の結果から1つのアルバムを選ぶ() throws Exception {

        // Setup
        AlbumForm album1 = new AlbumForm();
        album1.setAlbumName("Kid A");
        album1.setArtistName("Radiohead");

        AlbumForm album2 = new AlbumForm();
        album2.setAlbumName("OK Computer");
        album2.setArtistName("Radiohead");

        when(albumService.searchAlbums("Radiohead")).thenReturn(List.of(album1, album2));

        // Exercise / Verify
        mockMvc.perform(get("/AlbumArchive/add/confirm")
                .param("artistName", "Radiohead")
                .param("albumName", "OK Computer")
                .param("query", "Radiohead"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("albumDetails", album2));
    }

    @Test
    void testShowAddConfirmPage_一致する名前がない場合() throws Exception {

        // Setup
        AlbumForm album = new AlbumForm();
        album.setAlbumName("Kid A");
        album.setArtistName("Radiohead");

        when(albumService.searchAlbums("Radiohead")).thenReturn(List.of(album));

        // Exercise / Verify
        mockMvc.perform(get("/AlbumArchive/add/confirm")
                .param("artistName", "Radiohead")
                .param("albumName", "In Rainbows")
                .param("query", "Radiohead"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("albumDetails", (Object) null));

    }

    @Test
    void testShowAddPage_アルバム追加画面表示() throws Exception {
        // Exercise / Verify
        mockMvc.perform(get("/AlbumArchive/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("add"))
                .andExpect(model().attribute("activeTab", "add"));

    }

    @Test
    void testShowAddPageRedirect_アルバム登録リダイレクト処理() throws Exception {

        // Exercise / Verify
        mockMvc.perform(post("/AlbumArchive/add/register")
                .param("albumName", "Kid A")
                .param("artistName", "Radiohead"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/AlbumArchive/add"))
                .andExpect(flash().attribute("successMsg", "Added to your library."));

        verify(albumService, times(1)).addAlbum(any(AlbumForm.class));
    }

    @Test
    void testShowSearchResultPage_検索結果画面表示() throws Exception {
        AlbumForm album = new AlbumForm();
        album.setAlbumName("Kid A");
        album.setArtistName("Radiohead");

        when(albumService.searchAlbums("Radiohead")).thenReturn(List.of(album));

        // Exercise / Verify
        mockMvc.perform(get("/AlbumArchive/add/search").param("query", "Radiohead"))
                .andExpect(status().isOk())
                .andExpect(view().name("add"))
                .andExpect(model().attribute("albums", List.of(album)))
                .andExpect(model().attribute("searchQuery", "Radiohead"));
    }
}
