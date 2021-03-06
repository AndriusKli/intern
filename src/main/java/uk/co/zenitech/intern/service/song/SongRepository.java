package uk.co.zenitech.intern.service.song;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.co.zenitech.intern.entity.Song;

@Repository
public interface SongRepository extends JpaRepository<Song, Long> {
}
