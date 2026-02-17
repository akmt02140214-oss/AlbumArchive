package com.albumarchive.repository;

import java.util.List;

import com.albumarchive.entity.Album;

public interface AlbumRepository {
	
	List<Album> findRecentAlbum(int limit);
	
	void albumRegister(Album album);

}
