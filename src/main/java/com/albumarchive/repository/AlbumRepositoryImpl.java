package com.albumarchive.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.albumarchive.dto.AlbumDto;
import com.albumarchive.dto.TopAlbumResponse;
import com.albumarchive.entity.Album;
import com.albumarchive.entity.AlbumForm;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AlbumRepositoryImpl implements AlbumRepository {

    private final JdbcTemplate jdbcTemplate;

    //RestClientAPIフィールド
    private final RestClient restClient;

    @Value("${lastfm.api.key}")
    private String apiKey;

    @Value("${lastfm.api.url}")
    private String apiUrl;

    //検索キーワードに基づいてアルバム取得
    @Override
    public List<AlbumForm> searchAlbums(String query) {

        //APIリクエスト用URL作成
        String url = UriComponentsBuilder.fromUriString(apiUrl)
            .queryParam("method", "artist.gettopalbums")
            .queryParam("artist", query)
            .queryParam("api_key", apiKey)
            .queryParam("format", "json")
            .toUriString();

            //Last.fm APIはたくさんのデータを取得してくるため、一度そのデータをDTOで受ける
            //Getリクエスト送信とDTOへのマッピング
            TopAlbumResponse response = restClient.get()
                .uri(url)
                .retrieve()
                .body(TopAlbumResponse.class);

        //APIからのデータを全て受け取るDTOからAlbumDtoで使用するデータだけを受ける
        List<AlbumDto> dtoList = response.getTopalbums().getAlbum();

        //DTOからFormに詰め替え
        List<AlbumForm> albumList = new ArrayList<>();

        for (AlbumDto dto : dtoList) {
            AlbumForm album = new AlbumForm();
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

    @Override
    public void addAlbum(Album album) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("albums")
                .usingGeneratedKeyColumns("id");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("album_name", album.getAlbumName());
        parameters.put("artist_name", album.getArtistName());
        parameters.put("image_url", album.getImageUrl());
        parameters.put("rating", album.getRating());
        parameters.put("memo", album.getMemo());
        parameters.put("register_date", album.getRegisterDate());

        Number generatedId = insert.executeAndReturnKey(parameters);
        album.setId(generatedId.longValue());
    }
}
