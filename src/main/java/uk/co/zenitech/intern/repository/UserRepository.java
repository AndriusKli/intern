package uk.co.zenitech.intern.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.co.zenitech.intern.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
