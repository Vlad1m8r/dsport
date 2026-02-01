package ru.weu.dsport.service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.weu.dsport.domain.AppUser;
import ru.weu.dsport.exception.InvalidInitDataException;
import ru.weu.dsport.repository.AppUserRepository;

@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final AppUserRepository appUserRepository;

    public AppUser getOrCreateCurrentUser(long tgUserId) {
        return appUserRepository.findByTgUserId(tgUserId)
                .orElseGet(() -> {
                    OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
                    AppUser user = AppUser.builder()
                            .tgUserId(tgUserId)
                            .createdAt(now)
                            .updatedAt(now)
                            .build();
                    return appUserRepository.save(user);
                });
    }

    public AppUser getCurrentUser() {
        Long userId = CurrentUserContext.getCurrentUserId();
        if (userId == null) {
            throw new InvalidInitDataException("Current user is not available");
        }
        return appUserRepository.findById(userId)
                .orElseThrow(() -> new InvalidInitDataException("Current user is not available"));
    }
}
