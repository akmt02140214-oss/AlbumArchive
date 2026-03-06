package com.albumarchive.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.albumarchive.dto.ArtistRankingDto;
import com.albumarchive.dto.GenreRankingDto;
import com.albumarchive.entity.Album;
import com.albumarchive.entity.AlbumForm;
import com.albumarchive.entity.AlbumGenre;
import com.albumarchive.repository.AlbumRepository;
import com.albumarchive.repository.GenreRepository;

@ExtendWith(MockitoExtension.class)
public class AlbumServiceImplTest {

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private GenreRepository genreRepository;

    @InjectMocks
    private AlbumServiceImpl albumService;

    @Test
    public void testSearchAlbums_リポジトリの検索結果を取得() {

        // Setup
        AlbumForm form = new AlbumForm();
        form.setAlbumName("Kid A");
        when(albumRepository.searchAlbums("Radiohead")).thenReturn(List.of(form));

        // Exercise
        List<AlbumForm> result = albumService.searchAlbums("Radiohead");

        // Verify
        assertThat(result.size(), is(1));
        assertThat(result.get(0).getAlbumName(), is("Kid A"));
        verify(albumRepository, times(1)).searchAlbums("Radiohead");
    }

    @Test
    public void testAddAlbum_リポジトリのアルバム登録メソッドの確認() {

        // Setup
        AlbumForm form = new AlbumForm();
        form.setAlbumName("Kid A");
        form.setArtistName("Radiohead");
        form.setGenres(List.of("Electronic", "Rock"));
        form.setRating(5);
        form.setMemo("Good music!");

        // Exercise
        albumService.addAlbum(form);

        // Verify
        verify(albumRepository, times(1)).addAlbum(any(Album.class));
        verify(genreRepository, times(2)).addGenre(any(AlbumGenre.class));
    }

    @Test
    public void testSearchMyAlbums_オフセットとソート順の反映() {

        // Setup
        Album album = new Album();
        album.setAlbumName("OK Computer");
        when(albumRepository.searchMyAlbums(0, "newest")).thenReturn(List.of(album));

        // Exercise
        List<Album> result = albumService.searchMyAlbums(0, "newest");

        // Verify
        assertThat(result.size(), is(1));
        assertThat(result.get(0).getAlbumName(), is("OK Computer"));
        verify(albumRepository, times(1)).searchMyAlbums(0, "newest");
    }

    @Test
    public void testAddAlbum_ジャンルがnullの場合ジャンルは登録されない() {

        // Setup
        AlbumForm form = new AlbumForm();
        form.setAlbumName("Kid A");
        form.setArtistName("Radiohead");
        form.setGenres(null);

        // Exercise
        albumService.addAlbum(form);

        // Verify
        verify(albumRepository, times(1)).addAlbum(any(Album.class));
        verify(genreRepository, times(0)).addGenre(any(AlbumGenre.class));
    }

    @Test
    public void testGetTotalAlbumCount_リポジトリの値が返ってくる() {

        // Setup
        when(albumRepository.getTotalAlbumCount()).thenReturn(10);

        // Exercise
        int result = albumService.getTotalAlbumCount();

        // Verify
        assertThat(result, is(10));
        verify(albumRepository, times(1)).getTotalAlbumCount();
    }

    @Test
    public void testGetAlbumId_指定したIDのアルバムが取得できる() {

        // Setup
        Album expected = new Album();
        expected.setId(1L);
        expected.setAlbumName("Kid A");
        expected.setArtistName("Radiohead");
        when(albumRepository.getAlbumById(1L)).thenReturn(expected);

        // Exercise
        Album actual = albumService.getAlbumById(1L);

        // Verify
        assertThat(actual.getAlbumName(), is("Kid A"));
        assertThat(actual.getArtistName(), is("Radiohead"));

        verify(albumRepository, times(1)).getAlbumById(1L);
    }

    @Test
    public void testGetGenreByAlbumId_ジャンルのリストを取得() {

        // Setup
        List<String> genres = List.of("Electronic", "Rock");
        when(albumRepository.getGenresByAlbumId(1L)).thenReturn(genres);

        // Exercise
        List<String> result = albumService.getGenresByAlbumId(1L);

        // Verify
        assertThat(result.size(), is(2));
        assertThat(result.contains("Electronic"), is(true));
        assertThat(result.contains("Rock"), is(true));
        verify(albumRepository, times(1)).getGenresByAlbumId(1L);
    }

    @Test
    public void testUpdateAlbum_アルバム更新とジャンル削除() {

        // Setup
        Long albumId = 1L;
        AlbumForm form = new AlbumForm();
        form.setRating(3);
        form.setMemo("まあまあ");
        form.setGenres(List.of("Jazz", "Rock"));

        // Exercise
        albumService.updateAlbum(albumId, form);

        // Verify
        verify(albumRepository, times(1)).updateAlbum(any(Album.class));
        verify(albumRepository, times(1)).deleteGenresByAlbumId(albumId);
        verify(genreRepository, times(2)).addGenre(any(AlbumGenre.class));
    }

    @Test
    public void testUpdateAlbum_ジャンルがnullの場合はジャンル削除() {

        // Setup
        Long albumId = 1L;
        AlbumForm form = new AlbumForm();
        form.setRating(3);
        form.setMemo("ジャンルが分からない。");
        form.setGenres(null);

        // Exercise
        albumService.updateAlbum(albumId, form);

        // Verify
        verify(albumRepository, times(1)).updateAlbum(any(Album.class));
        verify(albumRepository, times(1)).deleteGenresByAlbumId(albumId);
        verify(genreRepository, times(0)).addGenre(any(AlbumGenre.class));
    }

    @Test
    public void testDeleteAlbum_アルバムとジャンルが両方削除される() {

        // Setup
        Long albumId = 1L;

        // Exercise
        albumService.deleteAlbum(albumId);

        // Verify
        verify(albumRepository, times(1)).deleteGenresByAlbumId(albumId);
        verify(albumRepository, times(1)).deleteAlbum(albumId);
    }

    @Test
    public void testGetRecentAlbums_リポジトリから取得したリストを返す() {

        // Setup
        Album album1 = new Album();
        album1.setAlbumName("Kid A");
        Album album2 = new Album();
        album2.setAlbumName("OK Computer");

        List<Album> expected = List.of(album1, album2);
        when(albumRepository.get5AlbumsOrderByRegisterDateDesc()).thenReturn(expected);

        // Exercise
        List<Album> result = albumService.getRecentAlbums();

        // Verify
        assertThat(result.size(), is(2));
        assertThat(result.get(0).getAlbumName(), is("Kid A"));
        assertThat(result.get(1).getAlbumName(), is("OK Computer"));
        verify(albumRepository, times(1)).get5AlbumsOrderByRegisterDateDesc();
    }

    @Test
    public void testGetTop3Artists_アーティストランキングの取得() {

        // Setup
        ArtistRankingDto dto1 = new ArtistRankingDto();
        dto1.setArtistName("Radiohead");
        dto1.setCount(10);

        ArtistRankingDto dto2 = new ArtistRankingDto();
        dto2.setArtistName("Beatles");
        dto2.setCount(5);

        ArtistRankingDto dto3 = new ArtistRankingDto();
        dto3.setArtistName("Kendrick lamar");
        dto3.setCount(1);
        when(albumRepository.getTop3Artists()).thenReturn(List.of(dto1, dto2, dto3));

        // Exercise
        List<ArtistRankingDto> result = albumService.getTop3Artists();

        // Verify
        assertThat(result.size(), is(3));
        assertThat(result.get(0).getArtistName(), is("Radiohead"));
        assertThat(result.get(1).getArtistName(), is("Beatles"));
        assertThat(result.get(2).getArtistName(), is("Kendrick lamar"));
        verify(albumRepository, times(1)).getTop3Artists();
    }

    @Test
    public void testGetTop3Genres_ジャンルランキングの取得() {

        // Setup
        GenreRankingDto dto1 = new GenreRankingDto();
        dto1.setGenre("Rock");
        dto1.setCount(10);

        GenreRankingDto dto2 = new GenreRankingDto();
        dto2.setGenre("Electronic");
        dto2.setCount(5);

        GenreRankingDto dto3 = new GenreRankingDto();
        dto3.setGenre("Pop");
        dto3.setCount(1);
        when(albumRepository.getTop3Genres()).thenReturn(List.of(dto1, dto2, dto3));

        // Exercise
        List<GenreRankingDto> result = albumService.getTop3Genres();

        // Verify
        assertThat(result.size(), is(3));
        assertThat(result.get(0).getGenre(), is("Rock"));
        assertThat(result.get(1).getGenre(), is("Electronic"));
        assertThat(result.get(2).getGenre(), is("Pop"));
        verify(albumRepository, times(1)).getTop3Genres();

    }
}
