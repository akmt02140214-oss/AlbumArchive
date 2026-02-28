package com.albumarchive.repository;

import java.util.List;

import com.albumarchive.entity.Album;
import com.albumarchive.entity.AlbumForm;

public interface AlbumRepository {

    List<AlbumForm> searchAlbums(String searchKeywords);

    void addAlbum(Album album);
}
