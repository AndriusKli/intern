package uk.co.zenitech.intern.unit;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.co.zenitech.intern.entity.Artist;
import uk.co.zenitech.intern.errorhandling.exceptions.EntityNotInDbException;
import uk.co.zenitech.intern.service.artist.ArtistRepository;
import uk.co.zenitech.intern.service.artist.ITunesArtistService;

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
        EntityNotInDbException exception = assertThrows(EntityNotInDbException.class, () ->
                ITunesArtistService.getArtist(5L));
        verify(artistRepository, never()).save(any());
        assertEquals("The requested artist with the id 5 was not found in the database.", exception.getMessage());
    }
}
