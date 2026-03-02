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

    @Override
    public List<Album> searchMyAlbums(int offset, String sort) {
        
        List<Album> myAlbums = albumRepository.searchMyAlbums(offset, sort);
        return myAlbums;
    }

    @Override
    public int getTotalAlbumCount() {
        return albumRepository.getTotalAlbumCount();
    }

    @Override
    public Album getAlbumById(Long id) {
        Album album = albumRepository.getAlbumById(id);
        return album;
    }

    @Override
    public List<String> getGenresByAlbumId(Long id) {
        List<String> genres = albumRepository.getGenresByAlbumId(id);
        return genres;

    }

    // 登録済みアルバム編集処理
    @Override
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
        List<Album> recentAlbums = albumRepository.get5AlbumsOrderByRegisterDateDesc();
        return recentAlbums;
        }

    // 登録したアーティストが多い順に3つ取得
    @Override
    public List<ArtistRankingDto> getTop3Artists() {
        List<ArtistRankingDto> top3Artists = albumRepository.getTop3Artists();
        return top3Artists;
    }

    @Override
    public List<GenreRankingDto> getTop3Genres() {
        List<GenreRankingDto> top3Genres = albumRepository.getTop3Genres();
        return top3Genres;
    }

    
}
