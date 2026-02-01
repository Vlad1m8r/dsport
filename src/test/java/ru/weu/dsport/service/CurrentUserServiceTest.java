package ru.weu.dsport.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.weu.dsport.domain.AppUser;
import ru.weu.dsport.exception.InvalidInitDataException;
import ru.weu.dsport.repository.AppUserRepository;

@ExtendWith(MockitoExtension.class)
class CurrentUserServiceTest {

    @Mock
    private AppUserRepository appUserRepository;

    @AfterEach
    void tearDown() {
        CurrentUserContext.clear();
    }

    @Test
    void getOrCreateCurrentUserReturnsExistingUser() {
        AppUser existing = AppUser.builder()
                .id(10L)
                .tgUserId(100L)
                .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
                .updatedAt(OffsetDateTime.now(ZoneOffset.UTC))
                .build();
        when(appUserRepository.findByTgUserId(100L)).thenReturn(Optional.of(existing));

        CurrentUserService service = new CurrentUserService(appUserRepository);
        AppUser result = service.getOrCreateCurrentUser(100L);

        assertThat(result).isSameAs(existing);
        verify(appUserRepository, never()).save(any());
    }

    @Test
    void getOrCreateCurrentUserCreatesWhenMissing() {
        when(appUserRepository.findByTgUserId(200L)).thenReturn(Optional.empty());
        when(appUserRepository.save(any())).thenAnswer(invocation -> {
            AppUser user = invocation.getArgument(0, AppUser.class);
            return AppUser.builder()
                    .id(20L)
                    .tgUserId(user.getTgUserId())
                    .createdAt(user.getCreatedAt())
                    .updatedAt(user.getUpdatedAt())
                    .build();
        });

        CurrentUserService service = new CurrentUserService(appUserRepository);
        AppUser result = service.getOrCreateCurrentUser(200L);

        assertThat(result.getId()).isEqualTo(20L);
        ArgumentCaptor<AppUser> captor = ArgumentCaptor.forClass(AppUser.class);
        verify(appUserRepository).save(captor.capture());
        AppUser saved = captor.getValue();
        assertThat(saved.getTgUserId()).isEqualTo(200L);
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    void getCurrentUserThrowsWhenContextMissing() {
        CurrentUserService service = new CurrentUserService(appUserRepository);

        assertThatThrownBy(service::getCurrentUser)
                .isInstanceOf(InvalidInitDataException.class)
                .hasMessageContaining("Current user");
    }

    @Test
    void getCurrentUserReturnsUserFromContext() {
        AppUser existing = AppUser.builder()
                .id(30L)
                .tgUserId(300L)
                .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
                .updatedAt(OffsetDateTime.now(ZoneOffset.UTC))
                .build();
        when(appUserRepository.findById(30L)).thenReturn(Optional.of(existing));
        CurrentUserContext.set(30L, 300L);

        CurrentUserService service = new CurrentUserService(appUserRepository);
        AppUser result = service.getCurrentUser();

        assertThat(result).isSameAs(existing);
    }
}
