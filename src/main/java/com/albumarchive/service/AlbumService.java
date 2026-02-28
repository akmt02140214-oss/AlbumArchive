package com.albumarchive.service;

import java.util.List;

import com.albumarchive.entity.AlbumForm;

public interface AlbumService {

    //アルバム検索処理
    List<AlbumForm> searchAlbums(String searchKeyword);

    //アルバム登録処理
    void addAlbum(AlbumForm albumForm);
}
