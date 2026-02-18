package com.albumarchive.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.albumarchive.dto.AlbumDto;
import com.albumarchive.dto.LastFmResponse;
import com.albumarchive.entity.Album;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AddAlbumRepositoryImpl implements AddAlbumRepository {

    //RestClientAPIフィールド
    private final RestClient restClient;
    @Value("${lastfm.api.key}")
    private String apiKey;

    @Value("${lastfm.api.url}")
    private String apiUrl;

    //検索キーワードに基づいてアルバム取得
    @Override
    public List<Album> searchAlbums(String query) {

        //APIリクエスト用URL作成
        String url = UriComponentsBuilder.fromUriString(apiUrl)
            .queryParam("method", "artist.gettopalbums")
            .queryParam("artist", query)
            .queryParam("api_key", apiKey)
            .queryParam("format", "json")
            .toUriString();

            //Getリクエスト送信とDTOへのマッピング
            LastFmResponse response = restClient.get()
                .uri(url)
                .retrieve()
                .body(LastFmResponse.class);
        //アルバム情報リスト取得        
        List<AlbumDto> dtoList = response.getTopalbums().getAlbum();

        //DTOからEntityに詰め替え
        List<Album> albumList = new ArrayList<>();

        for (AlbumDto dto : dtoList) {
            Album album = new Album();
            album.setAlbumName(dto.getAlbumName());
            album.setArtistName(dto.getArtistName());
            
            //largeサイズの画像取得
            if (dto.getImage() != null && !dto.getImage().isEmpty()) {
                for (AlbumDto.ImageDto imageDto : dto.getImage()) {
                    if ("extralarge".equals(imageDto.getSize())) {
                        album.setImageUrl(imageDto.getUrl());
                        break;
                    }
                }
            }
            albumList.add(album);
        }
        return albumList;
    }
}
