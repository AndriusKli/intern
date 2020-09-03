package uk.co.zenitech.intern.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.co.zenitech.intern.client.ITunedFeignClient;
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
public class ArtistServiceImp implements ArtistService {

    private final ResponseParser responseParser;
    private final ArtistRepository artistRepository;
    private final ITunedFeignClient iTunedFeignClient;
    private static final Logger logger = LoggerFactory.getLogger(ArtistServiceImp.class);

    @Autowired
    public ArtistServiceImp(ResponseParser responseParser, ArtistRepository artistRepository, ITunedFeignClient iTunedFeignClient) {
        this.responseParser = responseParser;
        this.artistRepository = artistRepository;
        this.iTunedFeignClient = iTunedFeignClient;
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
        return iTunedFeignClient.getResults(
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
                iTunedFeignClient.getById(id)
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
