package com.albumarchive.repository;

import java.util.List;

import com.albumarchive.entity.Album;

public interface AddAlbumRepository {

    List<Album> searchAlbums(String searchKeywords);

}
