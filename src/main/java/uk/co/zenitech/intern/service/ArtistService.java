package uk.co.zenitech.intern.service;

import uk.co.zenitech.intern.entity.Artist;

import java.util.List;

public interface ArtistService {
    List<Artist> getArtists(String artist, Long limit);
    Artist getArtist(Long id);
    void createArtist(Artist artist);
    void deleteArtist(Long id);
    void updateArtist(Long id, Artist artist);
}
