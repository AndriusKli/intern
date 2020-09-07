package uk.co.zenitech.intern.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.co.zenitech.intern.client.ITunesFeignClient;
import uk.co.zenitech.intern.client.musicparams.Attribute;
import uk.co.zenitech.intern.client.musicparams.Entity;
import uk.co.zenitech.intern.entity.Artist;
import uk.co.zenitech.intern.repository.ArtistRepository;
import uk.co.zenitech.intern.response.ITunesResponse;
import uk.co.zenitech.intern.serializer.ResponseParser;
import uk.co.zenitech.intern.serializer.WrapperType;

import javax.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ITunesArtistService implements ArtistService {

    private final ResponseParser responseParser;
    private final ArtistRepository artistRepository;
    private final ITunesFeignClient iTunesFeignClient;
    private static final Logger logger = LoggerFactory.getLogger(ITunesArtistService.class);

    @Autowired
    public ITunesArtistService(ResponseParser responseParser, ArtistRepository artistRepository, ITunesFeignClient iTunesFeignClient) {
        this.responseParser = responseParser;
        this.artistRepository = artistRepository;
        this.iTunesFeignClient = iTunesFeignClient;
    }

    @Override
    @Transactional
    public List<Artist> getArtists(String artistName, Long limit) {
        List<Artist> artists = responseParser.parse(
                Artist.class,
                WrapperType.ARTIST.getWrapper(),
                fetchArtistData(artistName, limit)
        );
        artistRepository.saveAll(artists);
        return artists;
    }

    private ResponseEntity<ITunesResponse> fetchArtistData(String artistName, Long limit) {
        return iTunesFeignClient.getResults(
                artistName,
                Entity.MUSIC_ARTIST.getValue(),
                Attribute.ARTIST_TERM.getValue(),
                limit);
    }

    @Override
    @Transactional
    public Artist getArtist(Long id) {
        Optional<Artist> artist = artistRepository.findById(id);
        if (artist.isEmpty()) {
            return fetchArtist(id);
        } else {
            return artist.get();
        }
//        return artistRepository.findById(id).orElse(fetchArtist(id));
    }

    private Artist fetchArtist(Long id) {
        List<Artist> artists = responseParser.parse(
                Artist.class,
                WrapperType.ARTIST.getWrapper(),
                iTunesFeignClient.getById(id)
        );
        if (artists.isEmpty()) {
            logger.warn("No artist found with the requested id: {}", id);
            throw new NoSuchElementException("No artist with the requested id found");
        } else {
            Artist artist = artists.get(0);
            artistRepository.save(artist);
            return artist;
        }
    }
}
