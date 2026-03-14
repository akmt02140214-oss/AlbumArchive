package com.albumarchive.dto;

import java.util.List;

import lombok.Data;

@Data
public class TopAlbumResponse {

    private TopAlbums topalbums;

    @Data
    public static class TopAlbums {
        private List<AlbumDto> album;
    }
}
