package com.albumarchive.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.albumarchive.dto.ArtistRankingDto;
import com.albumarchive.dto.GenreRankingDto;
import com.albumarchive.entity.Album;
import com.albumarchive.service.AlbumService;

@WebMvcTest(HomeController.class)
public class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AlbumService albumService;

    @Test
    void testShowHome_ホーム画面表示() throws Exception {

        // Setup
        Album album = new Album();
        album.setAlbumName("Kid A");

        ArtistRankingDto artist = new ArtistRankingDto();
        artist.setArtistName("Radiohead");

        GenreRankingDto genre = new GenreRankingDto();
        genre.setGenre("Electronic");

        when(albumService.getRecentAlbums()).thenReturn(List.of(album));
        when(albumService.getTop3Artists()).thenReturn(List.of(artist));
        when(albumService.getTop3Genres()).thenReturn(List.of(genre));

        // Exercise / Verify
        mockMvc.perform(get("/AlbumArchive"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attribute("myAlbums", List.of(album)))
                .andExpect(model().attribute("activeTab", "home"));
    }
}