package com.albumarchive.service;

import java.util.List;

import com.albumarchive.entity.Album;
import com.albumarchive.entity.AlbumForm;

public interface AlbumService {

    //アルバム検索処理(Add)
    List<AlbumForm> searchAlbums(String searchKeyword);

    //アルバム登録処理(Add)
    void addAlbum(AlbumForm albumForm);

    //登録済みアルバム一覧表示(Library)
    List<Album> searchMyAlbums(int pageCount, String sort);

    //登録済みアルバム数取得
    int getTotalAlbumCount();

    // 登録済みアルバム詳細情報取得
    Album getAlbumById(Long id);

    // 登録済みアルバムのジャンル情報取得
    List<String> getGenresByAlbumId(Long id);

    // 登録済みアルバムの編集処理
    void updateAlbum(Long id, AlbumForm albumForm);

}
