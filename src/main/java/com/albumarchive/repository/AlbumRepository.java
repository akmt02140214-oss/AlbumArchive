package com.albumarchive.repository;

import java.util.List;

import com.albumarchive.entity.Album;
import com.albumarchive.entity.AlbumForm;

public interface AlbumRepository {

    // アルバム検索機能
    List<AlbumForm> searchAlbums(String searchKeywords);

    // アルバム追加機能
    void addAlbum(Album album);

    // 登録済みアルバム取得機能
    List<Album> searchMyAlbums(int offset, String sort);

    // 登録済みアルバム数取得機能
    int getTotalAlbumCount();

    // 登録済みアルバム詳細情報取得機能
    Album getAlbumById(Long id);

    // 登録済みアルバムのジャンル取得機能
    List<String> getGenresByAlbumId(Long id);

    // 登録済みアルバム編集処理
    void updateAlbum(Album album);

    // 登録済みアルバム削除処理
    void deleteAlbum(Long id);

    // 登録済みアルバムのジャンル削除機能
    void deleteGenresByAlbumId(Long id);
}
