package com.albumarchive.service;

import java.util.List;

import com.albumarchive.entity.Album;

public interface AddAlbumService {

    //アルバム検索処理
    List<Album> searchAlbums(String searchKeyword);
}
