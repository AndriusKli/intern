package uk.co.zenitech.intern.service.artist;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.co.zenitech.intern.entity.Artist;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {
}
