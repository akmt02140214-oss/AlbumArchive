package com.albumarchive.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.albumarchive.entity.Album;
import com.albumarchive.entity.AlbumForm;
import com.albumarchive.entity.AlbumGenre;
import com.albumarchive.repository.AlbumRepository;
import com.albumarchive.repository.GenreRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlbumServiceImpl implements AlbumService {

    private final AlbumRepository albumRepository;

    private final GenreRepository genreRepository;

    @Override
    public List<AlbumForm> searchAlbums(String query) {
        List<AlbumForm> albumList = albumRepository.searchAlbums(query);
        return albumList;
    }
    
    @Override
    @Transactional
    public void addAlbum(AlbumForm albumForm) {
        // アルバム情報登録処理
        Album album = new Album();
        album.setAlbumName(albumForm.getAlbumName());
        album.setArtistName(albumForm.getArtistName());
        album.setImageUrl(albumForm.getImageUrl());
        album.setRating(albumForm.getRating());
        album.setMemo(albumForm.getMemo());
        album.setRegisterDate(LocalDateTime.now());

        albumRepository.addAlbum(album);

        // ジャンル情報登録処理
        if (albumForm.getGenres() != null) {
            for (String genre : albumForm.getGenres()) {
                AlbumGenre albumGenre = new AlbumGenre();
                albumGenre.setAlbumId(album.getId());
                albumGenre.setGenre(genre);

                genreRepository.addGenre(albumGenre);
            }
        }

    }
}
