package com.albumarchive.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.albumarchive.dto.ArtistRankingDto;
import com.albumarchive.dto.GenreRankingDto;
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
        return albumRepository.searchAlbums(query);
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

    @Override
    public List<Album> searchMyAlbums(int offset, String sort) {
        return albumRepository.searchMyAlbums(offset, sort);
    }

    @Override
    public int getTotalAlbumCount() {
        return albumRepository.getTotalAlbumCount();
    }

    @Override
    public Album getAlbumById(Long id) {
        return albumRepository.getAlbumById(id);
    }

    @Override
    public List<String> getGenresByAlbumId(Long id) {
        return albumRepository.getGenresByAlbumId(id);

    }

    // 登録済みアルバム編集処理
    @Override
    @Transactional
    public void updateAlbum(Long id, AlbumForm albumForm) {

        Album album = new Album();
        album.setId(id);
        album.setRating(albumForm.getRating());
        album.setMemo(albumForm.getMemo());
        albumRepository.updateAlbum(album);

        // 登録済みアルバムのジャンル削除
        albumRepository.deleteGenresByAlbumId(id);

        if (albumForm.getGenres() != null) {
            for (String genre : albumForm.getGenres()) {
                AlbumGenre albumGenre = new AlbumGenre();
                albumGenre.setAlbumId(id);
                albumGenre.setGenre(genre);
                genreRepository.addGenre(albumGenre);
            }
        }
    }

    // アルバム削除機能
    @Override
    @Transactional
    public void deleteAlbum(Long id) {

        albumRepository.deleteGenresByAlbumId(id);

        albumRepository.deleteAlbum(id);
    }

    // 最近登録したアルバム取得機能
    @Override
    public List<Album> getRecentAlbums() {
        return albumRepository.get5AlbumsOrderByRegisterDateDesc();
    }

    // 登録したアーティストが多い順に3つ取得
    @Override
    public List<ArtistRankingDto> getTop3Artists() {
        return albumRepository.getTop3Artists();
    }

    @Override
    public List<GenreRankingDto> getTop3Genres() {
        return albumRepository.getTop3Genres();
    }

}
