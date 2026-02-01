package ru.weu.dsport.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.HandlerExceptionResolver;
import ru.weu.dsport.lib.telegram.TgInitDataParser;
import ru.weu.dsport.service.CurrentUserService;

@Configuration
public class TgInitDataFilterConfig {

    @Bean
    public TgInitDataFilter tgInitDataFilter(
            HandlerExceptionResolver handlerExceptionResolver,
            TgInitDataParser tgInitDataParser,
            CurrentUserService currentUserService
    ) {
        return new TgInitDataFilter(tgInitDataParser, currentUserService, handlerExceptionResolver);
    }

    @Bean
    public FilterRegistrationBean<TgInitDataFilter> tgInitDataFilterRegistration(TgInitDataFilter tgInitDataFilter) {
        FilterRegistrationBean<TgInitDataFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(tgInitDataFilter);
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 10);
        registration.addUrlPatterns("/*");
        return registration;
    }
}
