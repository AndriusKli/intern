package uk.co.zenitech.intern.unit;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.co.zenitech.intern.entity.Artist;
import uk.co.zenitech.intern.repository.ArtistRepository;
import uk.co.zenitech.intern.service.ITunesArtistService;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@Disabled
public class ArtistServiceTests {

    @Mock
    ArtistRepository artistRepository;

    @InjectMocks
    ITunesArtistService ITunesArtistService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void fetchArtistRetrievesExistingArtist() {
        Artist artist = new Artist(5L, 2L, "Something");
        when(artistRepository.findById(anyLong())).thenReturn(java.util.Optional.of(artist));
        Artist result = ITunesArtistService.getArtist(5L);
        assertEquals(artist, result);
    }

    @Test
    void fetchArtistFailsUponNotFound() {
        when(artistRepository.findById(anyLong())).thenReturn(Optional.empty());
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () ->
                ITunesArtistService.getArtist(5L));
        verify(artistRepository, never()).save(any());
        assertEquals("No artist with the requested id found", exception.getMessage());
    }
}
