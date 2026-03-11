package com.albumarchive.repository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.JdbcTest;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;

import com.albumarchive.dto.AlbumDto;
import com.albumarchive.dto.ArtistRankingDto;
import com.albumarchive.dto.GenreRankingDto;
import com.albumarchive.dto.TopAlbumResponse;
import com.albumarchive.entity.Album;
import com.albumarchive.entity.AlbumForm;

@JdbcTest
public class AlbumRepositoryImplTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private RestClient restClient;

    private AlbumRepositoryImpl albumRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        albumRepository = new AlbumRepositoryImpl(jdbcTemplate, restClient);

        ReflectionTestUtils.setField(albumRepository, "apiKey", "test-api-key");
        ReflectionTestUtils.setField(albumRepository, "apiUrl", "http://ws.audioscrobbler.com/2.0/");

    }

    @Test
    void testAddAlbum_DBに登録されたアルバムのデータを確認() {

        // Setup
        Album album = new Album();
        album.setAlbumName("Kid A");
        album.setArtistName("Radiohead");
        album.setRating(5);
        album.setMemo("Great music.");
        album.setRegisterDate(LocalDateTime.now());

        // Exercise
        albumRepository.addAlbum(album);

        // Verify
        assertThat(album.getId() > 0, is(true));

        Album actual = jdbcTemplate.queryForObject("SELECT * FROM albums WHERE id = ?",
                new BeanPropertyRowMapper<>(Album.class),
                album.getId());
        assertThat(actual.getAlbumName(), is("Kid A"));
        assertThat(actual.getArtistName(), is("Radiohead"));

    }

    @Test
    void testDeleteAlbum_アルバムとジャンルが両方削除される() {

        // Setup
        jdbcTemplate.update("INSERT INTO albums (id, album_name, artist_name) VALUES (1, 'Kid A', 'Radiohead')");
        jdbcTemplate.update("INSERT INTO album_genres (album_id, genre) VALUES (1, 'Electronic')");

        // Exercise
        albumRepository.deleteAlbum(1L);

        // Verify
        int albumCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM albums WHERE id = 1", Integer.class);
        int genreCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM album_genres WHERE album_id = 1",
                Integer.class);
        assertThat(albumCount, is(0));
        assertThat(genreCount, is(0));

    }

    @Test
    void testDeleteGenresByAlbumId_アルバムのジャンル削除() {

        // Setup
        jdbcTemplate.update("INSERT INTO albums (id, album_name, artist_name) VALUES (1, 'Kid A', 'Radiohead')");
        jdbcTemplate.update("INSERT INTO album_genres (album_id, genre) VALUES (1, 'Rock')");

        // Exercise
        albumRepository.deleteGenresByAlbumId(1L);

        // Verify
        int count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM album_genres WHERE album_id = 1", Integer.class);
        assertThat(count, is(0));
    }

    @Test
    void testGet5AlbumsOrderByRegisterDateDesc_アルバムの最新5件取得() {

        // Setup
        List<Integer> ids = List.of(1, 2, 3, 4, 5, 6);

        for (int id : ids) {
            jdbcTemplate.update("INSERT INTO albums (id, album_name, artist_name, register_date) VALUES (?, ?, ?, ?)",
                    id, "Album" + id, "Artist" + id, LocalDateTime.now().plusDays(id));
        }

        // Exercise
        List<Album> result = albumRepository.get5AlbumsOrderByRegisterDateDesc();

        // Verify
        assertThat(result.size(), is(5));
        assertThat(result.get(0).getAlbumName(), is("Album6"));
        assertThat(result.get(1).getAlbumName(), is("Album5"));
        assertThat(result.get(2).getAlbumName(), is("Album4"));
        assertThat(result.get(3).getAlbumName(), is("Album3"));
        assertThat(result.get(4).getAlbumName(), is("Album2"));

    }

    @Test
    void testGetAlbumById_指定したIDのデータを取得() {

        // Setup
        jdbcTemplate.update("INSERT INTO albums (id, album_name, artist_name) VALUES (?, ?, ?)",
                10L, "OK Computer", "Radiohead");

        // Exercise
        Album result = albumRepository.getAlbumById(10L);

        // Verify
        assertThat(result.getAlbumName(), is("OK Computer"));
        assertThat(result.getArtistName(), is("Radiohead"));
    }

    @Test
    void testGetGenresByAlbumId_登録したアルバムのジャンルを取得() {

        // Setup
        jdbcTemplate.update("INSERT INTO albums (id, album_name, artist_name) VALUES (1, 'Kid A', 'Radiohead')");
        jdbcTemplate.update("INSERT INTO album_genres (album_id, genre) VALUES (1, 'Electronic')");
        jdbcTemplate.update("INSERT INTO album_genres (album_id, genre) VALUES (1, 'Rock')");

        // Exercise
        List<String> result = albumRepository.getGenresByAlbumId(1L);

        // Verify
        assertThat(result.size(), is(2));
        assertThat(result.contains("Electronic"), is(true));
        assertThat(result.contains("Rock"), is(true));
    }

    @Test
    void testGetTop3Artists_登録数が多い順に3アーティスト取得() {

        // Setup
        jdbcTemplate.update("INSERT INTO albums (album_name, artist_name) VALUES (?, ?)", "Pablo Honey", "Radiohead");
        jdbcTemplate.update("INSERT INTO albums (album_name, artist_name) VALUES (?, ?)", "The Bends", "Radiohead");
        jdbcTemplate.update("INSERT INTO albums (album_name, artist_name) VALUES (?, ?)", "OK Computer", "Radiohead");
        jdbcTemplate.update("INSERT INTO albums (album_name, artist_name) VALUES (?, ?)", "Kid A", "Radiohead");
        jdbcTemplate.update("INSERT INTO albums (album_name, artist_name) VALUES (?, ?)", "yeezus", "Kanye West");
        jdbcTemplate.update("INSERT INTO albums (album_name, artist_name) VALUES (?, ?)", "ye", "Kanye West");
        jdbcTemplate.update("INSERT INTO albums (album_name, artist_name) VALUES (?, ?)", "Donda", "Kanye West");
        jdbcTemplate.update("INSERT INTO albums (album_name, artist_name) VALUES (?, ?)", "Revolver", "Beatles");
        jdbcTemplate.update("INSERT INTO albums (album_name, artist_name) VALUES (?, ?)", "Abbey Road", "Beatles");
        jdbcTemplate.update("INSERT INTO albums (album_name, artist_name) VALUES (?, ?)", "Damn", "Kendrick lamar");

        // Exercise
        List<ArtistRankingDto> result = albumRepository.getTop3Artists();

        // Verify
        assertThat(result.size(), is(3));
        assertThat(result.get(0).getArtistName(), is("Radiohead"));
        assertThat(result.get(0).getCount(), is(4));
        assertThat(result.get(1).getArtistName(), is("Kanye West"));
        assertThat(result.get(1).getCount(), is(3));
        assertThat(result.get(2).getArtistName(), is("Beatles"));
        assertThat(result.get(2).getCount(), is(2));

    }

    @Test
    void testGetTop3Genres_ジャンルランキングの取得() {

        // Setup
        List<Integer> ids = List.of(1, 2, 3, 4, 5, 6);

        for (int id : ids) {
            jdbcTemplate.update("INSERT INTO albums (id, album_name, artist_name) VALUES (?, ?, ?)",
                    id, "Album" + id, "Artist" + id);
        }

        jdbcTemplate.update("INSERT INTO album_genres (album_id, genre) VALUES (1, 'Rock')");
        jdbcTemplate.update("INSERT INTO album_genres (album_id, genre) VALUES (2, 'Rock')");
        jdbcTemplate.update("INSERT INTO album_genres (album_id, genre) VALUES (3, 'Rock')");
        jdbcTemplate.update("INSERT INTO album_genres (album_id, genre) VALUES (4, 'Electronic')");
        jdbcTemplate.update("INSERT INTO album_genres (album_id, genre) VALUES (5, 'Electronic')");
        jdbcTemplate.update("INSERT INTO album_genres (album_id, genre) VALUES (6, 'Jazz')");

        // Exercise
        List<GenreRankingDto> result = albumRepository.getTop3Genres();

        // Verify
        assertThat(result.get(0).getGenre(), is("Rock"));
        assertThat(result.get(0).getCount(), is(3));
        assertThat(result.get(1).getGenre(), is("Electronic"));
        assertThat(result.get(1).getCount(), is(2));
        assertThat(result.get(2).getGenre(), is("Jazz"));
        assertThat(result.get(2).getCount(), is(1));

    }

    @Test
    void testGetTotalAlbumCount_アルバムの全件数取得() {

        // Setup
        jdbcTemplate.update("INSERT INTO albums (album_name, artist_name) VALUES (?, ?)", "Kid A", "Radiohead");
        jdbcTemplate.update("INSERT INTO albums (album_name, artist_name) VALUES (?, ?)", "ye", "Kanye West");
        jdbcTemplate.update("INSERT INTO albums (album_name, artist_name) VALUES (?, ?)", "Revolver", "Beatles");

        // Exercise
        int count = albumRepository.getTotalAlbumCount();

        // Verify
        assertThat(count, is(3));

    }

    @Test
    void testSearchAlbums_APIから取得したデータをフォームに変換() {

        // Setup
        TopAlbumResponse mock = new TopAlbumResponse();
        TopAlbumResponse.TopAlbums topAlbums = new TopAlbumResponse.TopAlbums();

        AlbumDto dto = new AlbumDto();
        dto.setAlbumName("Kid A");

        AlbumDto.ArtistDto artistDto = new AlbumDto.ArtistDto();
        artistDto.setName("Radiohead");
        dto.setArtist(artistDto);

        AlbumDto.ImageDto imageDto = new AlbumDto.ImageDto();
        imageDto.setSize("extralarge");
        imageDto.setUrl("http://example.com/image.jpg");
        dto.setImage(List.of(imageDto));

        topAlbums.setAlbum(List.of(dto));
        mock.setTopalbums(topAlbums);

        org.mockito.Mockito.when(restClient.get()
                .uri(org.mockito.ArgumentMatchers.anyString())
                .retrieve()
                .body(TopAlbumResponse.class)).thenReturn(mock);

        // Exercise
        List<AlbumForm> result = albumRepository.searchAlbums("Radiohead");

        // Verify
        assertThat(result.size(), is(1));
        assertThat(result.get(0).getAlbumName(), is("Kid A"));
        assertThat(result.get(0).getArtistName(), is("Radiohead"));
        assertThat(result.get(0).getImageUrl(), is("http://example.com/image.jpg"));
    }

    @Test
    void testSearchAlbums_imageSizeがextralargeを複数のサイズから取得() {

        // Setup
        TopAlbumResponse mock = new TopAlbumResponse();
        TopAlbumResponse.TopAlbums topAlbums = new TopAlbumResponse.TopAlbums();

        AlbumDto dto = new AlbumDto();
        dto.setAlbumName("Kid A");

        AlbumDto.ArtistDto artistDto = new AlbumDto.ArtistDto();
        artistDto.setName("Radiohead");
        dto.setArtist(artistDto);

        AlbumDto.ImageDto imageDto1 = new AlbumDto.ImageDto();
        imageDto1.setSize("small");
        imageDto1.setUrl("http://example.com/small.jpg");

        AlbumDto.ImageDto imageDto2 = new AlbumDto.ImageDto();
        imageDto2.setSize("extralarge");
        imageDto2.setUrl("http://example.com/target.jpg");

        dto.setImage(List.of(imageDto1, imageDto2));

        topAlbums.setAlbum(List.of(dto));
        mock.setTopalbums(topAlbums);

        org.mockito.Mockito.when(restClient.get()
                .uri(org.mockito.ArgumentMatchers.anyString())
                .retrieve()
                .body(TopAlbumResponse.class)).thenReturn(mock);

        // Exercise
        List<AlbumForm> result = albumRepository.searchAlbums("Radiohead");

        // Verify
        assertThat(result.get(0).getImageUrl(), is("http://example.com/target.jpg"));
    }

    @Test
    void testSearchAlbums_imageがnullの場合imageUrlはnull() {

        // Setup
        TopAlbumResponse mock = new TopAlbumResponse();
        TopAlbumResponse.TopAlbums topAlbums = new TopAlbumResponse.TopAlbums();

        AlbumDto dto = new AlbumDto();
        dto.setAlbumName("None");

        AlbumDto.ArtistDto artistDto = new AlbumDto.ArtistDto();
        artistDto.setName("Radiohead");
        dto.setArtist(artistDto);

        // nullのリスト
        dto.setImage(new ArrayList<>());

        topAlbums.setAlbum(List.of(dto));
        mock.setTopalbums(topAlbums);

        org.mockito.Mockito.when(restClient.get()
                .uri(org.mockito.ArgumentMatchers.anyString())
                .retrieve()
                .body(TopAlbumResponse.class)).thenReturn(mock);

        // Exercise
        List<AlbumForm> result = albumRepository.searchAlbums("Radiohead");

        // Verify
        assertThat(result.get(0).getAlbumName(), is("None"));
        assertThat(result.get(0).getImageUrl(), is((String) null));

    }

    @Test
    void testSearchAlbums_extralargeが存在しない場合imageUrlはnull() {

        // Setup
        TopAlbumResponse mock = new TopAlbumResponse();
        TopAlbumResponse.TopAlbums topAlbums = new TopAlbumResponse.TopAlbums();

        AlbumDto dto = new AlbumDto();
        dto.setAlbumName("Kid A");

        AlbumDto.ArtistDto artistDto = new AlbumDto.ArtistDto();
        artistDto.setName("Radiohead");
        dto.setArtist(artistDto);

        AlbumDto.ImageDto imageDto1 = new AlbumDto.ImageDto();
        imageDto1.setSize("small");
        imageDto1.setUrl("http://example.com/small.jpg");

        dto.setImage(List.of(imageDto1));

        topAlbums.setAlbum(List.of(dto));
        mock.setTopalbums(topAlbums);

        org.mockito.Mockito.when(restClient.get()
                .uri(org.mockito.ArgumentMatchers.anyString())
                .retrieve()
                .body(TopAlbumResponse.class)).thenReturn(mock);

        // Exercise
        List<AlbumForm> result = albumRepository.searchAlbums("Radiohead");

        // Verify
        assertThat(result.get(0).getImageUrl(), is((String) null));

    }

    @Test
    void testSearchAlbums_imageがnullの場合でも例外が発生しない() {

        // Setup
        TopAlbumResponse mock = new TopAlbumResponse();
        TopAlbumResponse.TopAlbums topAlbums = new TopAlbumResponse.TopAlbums();

        AlbumDto dto = new AlbumDto();
        dto.setAlbumName("Kid A");

        AlbumDto.ArtistDto artistDto = new AlbumDto.ArtistDto();
        artistDto.setName("Radiohead");
        dto.setArtist(artistDto);

        // nullをセット
        dto.setImage(null);

        topAlbums.setAlbum(List.of(dto));
        mock.setTopalbums(topAlbums);

        org.mockito.Mockito.when(restClient.get()
                .uri(org.mockito.ArgumentMatchers.anyString())
                .retrieve()
                .body(TopAlbumResponse.class)).thenReturn(mock);

        // Exercise
        List<AlbumForm> result = albumRepository.searchAlbums("Radiohead");

        // Verify
        assertThat(result.size(), is(1));
        assertThat(result.get(0).getAlbumName(), is("Kid A"));
        assertThat(result.get(0).getArtistName(), is("Radiohead"));
        assertThat(result.get(0).getImageUrl(), is((String) null));
    }

    @Test
    void testSearchMyAlbums_登録日の降順でソートする() {

        // Setup
        jdbcTemplate.update("INSERT INTO albums (album_name, artist_name, register_date) VALUES (?, ?, ?)",
                "OK Computer", "Radiohead", LocalDateTime.now().plusDays(1));
        jdbcTemplate.update("INSERT INTO albums (album_name, artist_name, register_date) VALUES (?, ?, ?)",
                "Kid A", "Radiohead", LocalDateTime.now().plusDays(2));

        // Exercise
        List<Album> result = albumRepository.searchMyAlbums(0, "newest");

        // Verify
        assertThat(result.get(0).getAlbumName(), is("Kid A"));
        assertThat(result.get(1).getAlbumName(), is("OK Computer"));

    }

    @Test
    void testSearchMyAlbums_登録日の昇順でソートする() {

        // Setup
        jdbcTemplate.update("INSERT INTO albums (album_name, artist_name, register_date) VALUES (?, ?, ?)",
                "OK Computer", "Radiohead", LocalDateTime.now().plusDays(1));
        jdbcTemplate.update("INSERT INTO albums (album_name, artist_name, register_date) VALUES (?, ?, ?)",
                "Kid A", "Radiohead", LocalDateTime.now().plusDays(2));

        // Exercise
        List<Album> result = albumRepository.searchMyAlbums(0, "oldest");

        // Verify
        assertThat(result.get(0).getAlbumName(), is("OK Computer"));
        assertThat(result.get(1).getAlbumName(), is("Kid A"));

    }

    @Test
    void testSearchMyAlbums_評価順でソートする() {

        // Setup
        jdbcTemplate.update("INSERT INTO albums (album_name, artist_name, rating) VALUES (?, ?, ?)",
                "OK Computer", "Radiohead", 5);
        jdbcTemplate.update("INSERT INTO albums (album_name, artist_name, rating) VALUES (?, ?, ?)",
                "Kid A", "Radiohead", 4);
        jdbcTemplate.update("INSERT INTO albums (album_name, artist_name, rating) VALUES (?, ?, ?)",
                "In Rainbows", "Radiohead", 3);
        jdbcTemplate.update("INSERT INTO albums (album_name, artist_name, rating) VALUES (?, ?, ?)",
                "The Bends", "Radiohead", 2);
        jdbcTemplate.update("INSERT INTO albums (album_name, artist_name, rating) VALUES (?, ?, ?)",
                "Pablo Honey", "Radiohead", 1);

        // Exercise
        List<Album> result = albumRepository.searchMyAlbums(0, "rating");

        // Verify
        assertThat(result.get(0).getAlbumName(), is("OK Computer"));
        assertThat(result.get(1).getAlbumName(), is("Kid A"));
        assertThat(result.get(2).getAlbumName(), is("In Rainbows"));
        assertThat(result.get(3).getAlbumName(), is("The Bends"));
        assertThat(result.get(4).getAlbumName(), is("Pablo Honey"));
    }

    @Test
    void testUpdateAlbum_評価とメモの更新() {

        // Setup
        jdbcTemplate.update(
                "INSERT INTO albums (id, album_name, artist_name, rating, memo) VALUES (1L, 'Kid A', 'Radiohead', 3, 'Great')");

        Album album = new Album();
        album.setId(1L);
        album.setRating(5);
        album.setMemo("Great!!!");

        // Exercise
        albumRepository.updateAlbum(album);

        // Verify
        Album actual = jdbcTemplate.queryForObject("SELECT * FROM albums WHERE id = 1",
                new BeanPropertyRowMapper<>(Album.class));
        assertThat(actual.getRating(), is(5));
        assertThat(actual.getMemo(), is(("Great!!!")));

    }
}
