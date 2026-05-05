package az.idtech.bookmanagement.dao.repository;

import az.idtech.bookmanagement.dao.entities.UserEntity;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);

    @Cacheable(cacheNames = "users", key = "userCount")
    @Query("SELECT COUNT(u) from UserEntity u")
    Long findUserCount();
}
