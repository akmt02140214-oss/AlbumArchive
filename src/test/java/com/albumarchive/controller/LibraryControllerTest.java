package com.albumarchive.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
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

import com.albumarchive.entity.Album;
import com.albumarchive.entity.AlbumForm;
import com.albumarchive.service.AlbumService;

@WebMvcTest(LibraryController.class)
public class LibraryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AlbumService albumService;

    @Test
    void testDeleteAlbum_アルバム削除処理() throws Exception {

        // Setup
        Long albumId = 1L;

        // Exercise / Verify
        mockMvc.perform(post("/AlbumArchive/library/delete")
                .param("id", albumId.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/AlbumArchive/library"))
                .andExpect(flash().attribute("message", "Album deleted."));

        verify(albumService, times(1)).deleteAlbum(albumId);
    }

    @Test
    void testShowLibraryDetailsPage_アルバム詳細画面表示() throws Exception {

        // Setup
        Long albumId = 1L;
        Album album = new Album();
        album.setId(albumId);
        album.setAlbumName("Kid A");

        List<String> genres = List.of("Electronic", "Rock");

        when(albumService.getAlbumById(albumId)).thenReturn(album);
        when(albumService.getGenresByAlbumId(albumId)).thenReturn(genres);

        // Exercise / Verify
        mockMvc.perform(get("/AlbumArchive/library/details")
                .param("id", albumId.toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("library-details"))
                .andExpect(model().attribute("album", album))
                .andExpect(model().attribute("genres", genres))
                .andExpect(model().attribute("activeTab", "library"));

    }

    @Test
    void testShowLibraryPage_ライブラリ画面表示() throws Exception {

        // Setup
        Album album = new Album();
        album.setAlbumName("Kid A");
        album.setArtistName("Radiohead");

        List<Album> albums = List.of(album);

        when(albumService.getTotalAlbumCount()).thenReturn(1);
        when(albumService.searchMyAlbums(anyInt(), anyString())).thenReturn(albums);

        // Exercise / Verify
        mockMvc.perform(get("/AlbumArchive/library")
                .param("pageCount", "0")
                .param("sort", "newest"))
                .andExpect(status().isOk())
                .andExpect(view().name("library"))
                .andExpect(model().attribute("myAlbums", albums))
                .andExpect(model().attribute("totalPages", 1))
                .andExpect(model().attribute("activeTab", "library"));

    }

    @Test
    void testShowLibraryPage_アルバムが0件の場合にtotalPagesが1になる() throws Exception {

        // Setup
        when(albumService.getTotalAlbumCount()).thenReturn(0);
        when(albumService.searchMyAlbums((anyInt()), anyString())).thenReturn(List.of());

        // Exercise / Verify
        mockMvc.perform(get("/AlbumArchive/library")
                .param("pageCount", "0")
                .param("sort", "newest"))
                .andExpect(status().isOk())
                .andExpect(view().name("library"))
                .andExpect(model().attribute("myAlbums", List.of()))
                .andExpect(model().attribute("totalPages", 1))
                .andExpect(model().attribute("activeTab", "library"));
    }

    @Test
    void testShowLibraryPageRedirect_アルバム編集処理() throws Exception {

        // Setup
        Long albumId = 1L;

        // Exercise / Verify
        mockMvc.perform(post("/AlbumArchive/library/update")
                .param("id", albumId.toString())
                .param("albumName", "Kid A")
                .param("artistName", "Radiohead"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/AlbumArchive/library/details?id=" + albumId))
                .andExpect(flash().attribute("message", "Album updated."));

        verify(albumService, times(1)).updateAlbum(eq(albumId), any(AlbumForm.class));
    }
}
