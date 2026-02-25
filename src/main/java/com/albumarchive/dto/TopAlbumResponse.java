package com.albumarchive.dto;

import java.util.List;

import lombok.Data;

@Data
public class TopAlbumResponse {

    //アルバム名でも検索できる機能を実装する可能性があるためコメントアウト
    // private Results results;

    // @Data
    // public static class Results {
    //     private AlbumMatches albummatches;
    // }

    // @Data
    // public static class AlbumMatches {
    //     private List<AlbumDto> album;
    // }
    
    private TopAlbums topalbums;

    @Data
    public static class TopAlbums {
        private List<AlbumDto> album;
    }
}
