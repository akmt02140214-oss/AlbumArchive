package com.albumarchive.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.greaterThan;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.albumarchive.entity.AlbumForm;

@SpringBootTest
@Transactional
public class AlbumServiceImplTest {

    @Autowired
    private AlbumService albumService;

    @Test
    public void testAddAlbum_アルバムを追加すると総数が増える() {

        // Setup
        int beforeCount = albumService.getTotalAlbumCount();

        AlbumForm form = new AlbumForm();
        form.setAlbumName("Kid A");
        form.setArtistName("Radiohead");
        form.setGenres(List.of("Electronic"));
        form.setRating(5);

        // Exercise
        albumService.addAlbum(form);

        // Verify
        int afterCount = albumService.getTotalAlbumCount();
        assertThat(afterCount, is(beforeCount + 1));
    }
}
