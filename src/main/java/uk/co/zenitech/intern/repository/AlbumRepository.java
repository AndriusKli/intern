package uk.co.zenitech.intern.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.co.zenitech.intern.entity.Album;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {
}
