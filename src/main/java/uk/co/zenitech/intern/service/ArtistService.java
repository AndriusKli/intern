package uk.co.zenitech.intern.service;

import uk.co.zenitech.intern.entity.Artist;

import java.util.List;

public interface ArtistService {
    List<Artist> getArtistByName(String artist);
}
