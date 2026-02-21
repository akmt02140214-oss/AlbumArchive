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

            //Last.fm APIはたくさんのデータを取得してくるため、一度そのデータをDTOで受ける
            //Getリクエスト送信とDTOへのマッピング
            LastFmResponse response = restClient.get()
                .uri(url)
                .retrieve()
                .body(LastFmResponse.class);

        //APIからのデータを全て受け取るDTOからAlbumDtoで使用するデータだけを受ける
        List<AlbumDto> dtoList = response.getTopalbums().getAlbum();

        //DTOからEntityに詰め替え
        List<Album> albumList = new ArrayList<>();

        for (AlbumDto dto : dtoList) {
            Album album = new Album();
            album.setAlbumName(dto.getAlbumName());
            album.setArtistName(dto.getArtistName());
            
            //Last.fm APIは複数のアルバムジャケットサイズを返すため、解像度を選別する
            //extralargeサイズ(300×300)を解像度に選択
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
