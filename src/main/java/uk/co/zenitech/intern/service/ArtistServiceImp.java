package uk.co.zenitech.intern.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.zenitech.intern.consumer.ITunesConsumer;
import uk.co.zenitech.intern.entity.Artist;
import uk.co.zenitech.intern.repository.ArtistRepository;
import uk.co.zenitech.intern.serializer.ResponseParser;

import javax.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ArtistServiceImp implements ArtistService {

    private ResponseParser responseParser;
    private ITunesConsumer iTunesConsumer;
    private ArtistRepository artistRepository;
    Logger logger = LoggerFactory.getLogger(ArtistServiceImp.class);

    @Autowired
    public ArtistServiceImp(ResponseParser responseParser, ITunesConsumer iTunesConsumer, ArtistRepository artistRepository) {
        this.responseParser = responseParser;
        this.iTunesConsumer = iTunesConsumer;
        this.artistRepository = artistRepository;
    }

    @Override
    @Transactional
    public List<Artist> getArtists(String artistName) {
        List<Artist> artists = responseParser.parse(
                Artist.class,
                "\"artist\"",
                iTunesConsumer.fetchData("musicArtist", artistName, "artistTerm"));
        artistRepository.saveAll(artists);
        return artists;
    }

    @Override
    @Transactional
    public Artist getArtist(Long id) {
        Optional<Artist> repoArtist = artistRepository.findById(id);
        if (repoArtist.isEmpty()) {
            List<Artist> artists = responseParser.parse(
                    Artist.class,
                    "\"artist\"",
                    iTunesConsumer.fetchById(id)
            );
            if (artists.isEmpty()) {
                logger.warn("No artist found with the requested id: {}", id);
                throw new NoSuchElementException("No artist with the requested id found");
            } else {
                Artist artist = artists.get(0);
                artistRepository.save(artist);
                return artist;
            }
        } else {
            return repoArtist.get();
        }
    }
}
