package ru.weu.dsport.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.HandlerExceptionResolver;
import ru.weu.dsport.domain.AppUser;
import ru.weu.dsport.exception.InvalidInitDataException;
import ru.weu.dsport.lib.telegram.TgInitDataParser;
import ru.weu.dsport.service.CurrentUserContext;
import ru.weu.dsport.service.CurrentUserService;

@ExtendWith(MockitoExtension.class)
class TgInitDataFilterTest {

    @Mock
    private TgInitDataParser tgInitDataParser;

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private HandlerExceptionResolver handlerExceptionResolver;

    @Mock
    private FilterChain filterChain;

    @AfterEach
    void tearDown() {
        CurrentUserContext.clear();
    }

    @Test
    void doFilterRejectsMissingHeader() throws ServletException, IOException {
        TgInitDataFilter filter = new TgInitDataFilter(tgInitDataParser, currentUserService, handlerExceptionResolver);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, filterChain);

        verify(filterChain, never()).doFilter(any(), any());
        ArgumentCaptor<Exception> captor = ArgumentCaptor.forClass(Exception.class);
        verify(handlerExceptionResolver).resolveException(eq(request), eq(response), eq(null), captor.capture());
        assertThat(captor.getValue()).isInstanceOf(InvalidInitDataException.class);
        assertThat(CurrentUserContext.getCurrentUserId()).isNull();
    }

    @Test
    void doFilterSetsContextAndContinues() throws ServletException, IOException {
        TgInitDataFilter filter = new TgInitDataFilter(tgInitDataParser, currentUserService, handlerExceptionResolver);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Tg-Init-Data", "user=payload");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(tgInitDataParser.parseUserId("user=payload")).thenReturn(500L);
        AppUser user = AppUser.builder()
                .id(50L)
                .tgUserId(500L)
                .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
                .updatedAt(OffsetDateTime.now(ZoneOffset.UTC))
                .build();
        when(currentUserService.getOrCreateCurrentUser(500L)).thenReturn(user);

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(handlerExceptionResolver, never()).resolveException(eq(request), eq(response), eq(null), any());
        assertThat(request.getAttribute(CurrentUserContext.CURRENT_USER_ID_ATTRIBUTE)).isEqualTo(50L);
        assertThat(request.getAttribute(CurrentUserContext.CURRENT_TG_USER_ID_ATTRIBUTE)).isEqualTo(500L);
        assertThat(CurrentUserContext.getCurrentUserId()).isNull();
    }
}
