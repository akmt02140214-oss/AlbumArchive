package com.albumarchive.dto;

import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class AlbumDtoTest {
    @Test
    public void testGetArtistName_アーティストが設定されている場合はその名前が返ること() {

        // Setup
        AlbumDto album = new AlbumDto();
        AlbumDto.ArtistDto artist = new AlbumDto.ArtistDto();
        artist.setName("Beatles");
        album.setArtist(artist);

        // Exercise
        String result = album.getArtistName();

        // Verify
        assertThat(result, is("Beatles"));
    }

    @Test
    public void getArtistName_アーティストがnullの場合はnullが返ること() {

        // Setup
        AlbumDto album = new AlbumDto();
        album.setArtist(null);

        // Exercise
        String result = album.getArtistName();

        // Verify
        assertThat(result, is(nullValue()));

    }
}
