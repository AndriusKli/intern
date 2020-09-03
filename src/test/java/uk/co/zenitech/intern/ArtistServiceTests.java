package uk.co.zenitech.intern;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.co.zenitech.intern.client.ITunedFeignClient;
import uk.co.zenitech.intern.entity.Artist;
import uk.co.zenitech.intern.repository.ArtistRepository;
import uk.co.zenitech.intern.response.ITunesResponse;
import uk.co.zenitech.intern.serializer.ResponseParser;
import uk.co.zenitech.intern.serializer.WrapperType;
import uk.co.zenitech.intern.service.ArtistServiceImp;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ArtistServiceTests {

    @Mock
    ArtistRepository artistRepository;

    @Mock
    ITunedFeignClient iTunedFeignClient;

    @Mock
    ResponseParser responseParser;

    @InjectMocks
    ArtistServiceImp artistServiceImp;

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @Disabled
    void getArtistsSavesResults() {
        List<Artist> artists = Arrays.asList(
                new Artist(1L, 1L, "John"),
                new Artist(2L, 2L, "Johny"));
        when(responseParser.parse(Artist.class, WrapperType.ARTIST.getWrapper(), new ResponseEntity<>(new ITunesResponse(), HttpStatus.OK))).thenReturn(artists);
        when(artistRepository.saveAll(any())).thenReturn(artists);
        List<Artist> results = artistServiceImp.getArtists("Jo", 2L);
        assertEquals(artists, results);
        verify(artistRepository, times(1)).saveAll(any());
    }

    @Test
    void fetchArtistRetrievesExistingArtist() {
        Artist artist = new Artist(5L, 2L, "Something");
        when(artistRepository.findById(anyLong())).thenReturn(java.util.Optional.of(artist));
        Artist result = artistServiceImp.getArtist(5L);
        assertEquals(artist, result);
    }

    @Test
    void fetchArtistFailsUponNotFound() {
        when(artistRepository.findById(anyLong())).thenReturn(Optional.empty());
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () ->
                artistServiceImp.getArtist(5L));
        verify(artistRepository, times(0)).save(any());
        assertEquals("No artist with the requested id found", exception.getMessage());
    }
}
