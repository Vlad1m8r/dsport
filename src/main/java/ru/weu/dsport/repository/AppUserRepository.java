package ru.weu.dsport.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.weu.dsport.domain.AppUser;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByTgUserId(Long tgUserId);
}
