package com.albumarchive.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.albumarchive.entity.Album;
import com.albumarchive.repository.AddAlbumRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AddAlbumServiceImpl implements AddAlbumService {

    private final AddAlbumRepository addAlbumRepository;
    @Override
    public List<Album> searchAlbums(String query) {
        List<Album> albumList = addAlbumRepository.searchAlbums(query);
        return albumList;
    }

}
