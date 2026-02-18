package com.albumarchive.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AlbumDto {

    @JsonProperty("artist")
    private ArtistDto artist;
    
    private List<ImageDto> image;
    
    public String getArtistName(){
        return artist != null ? artist.getName() : null;
    }

    @Data
    public static class ArtistDto {
        private String name;
        private String url;
    }
    @JsonProperty("name")
    private String albumName;

    // @JsonProperty("artist")
    // private String artistName;


    @Data
    public static class ImageDto {
        @JsonProperty("#text")
        private String url;
        private String size;
    }
    }
