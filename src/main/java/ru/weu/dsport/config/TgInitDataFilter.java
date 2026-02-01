package ru.weu.dsport.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import ru.weu.dsport.domain.AppUser;
import ru.weu.dsport.exception.InvalidInitDataException;
import ru.weu.dsport.lib.telegram.TgInitDataParser;
import ru.weu.dsport.service.CurrentUserContext;
import ru.weu.dsport.service.CurrentUserService;

public class TgInitDataFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(TgInitDataFilter.class);

    private final TgInitDataParser tgInitDataParser;
    private final CurrentUserService currentUserService;
    private final HandlerExceptionResolver handlerExceptionResolver;

    public TgInitDataFilter(
            TgInitDataParser tgInitDataParser,
            CurrentUserService currentUserService,
            HandlerExceptionResolver handlerExceptionResolver
    ) {
        this.tgInitDataParser = tgInitDataParser;
        this.currentUserService = currentUserService;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String initData = request.getHeader("X-Tg-Init-Data");
        boolean initDataPresent = StringUtils.hasText(initData);
        log.debug("initData present: {}", initDataPresent);

        try {
            if (!initDataPresent) {
                throw new InvalidInitDataException("X-Tg-Init-Data header is missing");
            }

            long tgUserId = tgInitDataParser.parseUserId(initData);
            AppUser currentUser = currentUserService.getOrCreateCurrentUser(tgUserId);
            CurrentUserContext.set(currentUser.getId(), tgUserId);
            request.setAttribute(CurrentUserContext.CURRENT_USER_ID_ATTRIBUTE, currentUser.getId());
            request.setAttribute(CurrentUserContext.CURRENT_TG_USER_ID_ATTRIBUTE, tgUserId);

            filterChain.doFilter(request, response);
        } catch (InvalidInitDataException ex) {
            handlerExceptionResolver.resolveException(request, response, null, ex);
        } finally {
            CurrentUserContext.clear();
        }
    }
}
