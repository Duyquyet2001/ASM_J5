package com.example.demo.ASM.Config;

import com.example.demo.ASM.Interceptor.AuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.util.Locale;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private static final Logger log = LoggerFactory.getLogger(WebConfig.class);
    private final AuthInterceptor authInterceptor; // ✅ thêm Interceptor mới

    @Bean
    public LocaleResolver localeResolver() {
        CookieLocaleResolver resolver = new CookieLocaleResolver();
        resolver.setDefaultLocale(Locale.forLanguageTag("en"));
        resolver.setCookieName("lang");
        resolver.setCookieMaxAge(60 * 60 * 24 * 30);
        resolver.setCookiePath("/");
        return resolver;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("lang");
        log.info("✅ LocaleChangeInterceptor registered — paramName='lang'");
        return interceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // ✅ i18n interceptor (đa ngôn ngữ)
        registry.addInterceptor(localeChangeInterceptor());

        // ✅ Auth interceptor (kiểm tra đăng nhập)
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/phieu-muon/**", "/thiet-bi/**") // đường cần bảo vệ
                .excludePathPatterns("/login", "/logout", "/css/**", "/js/**", "/images/**", "/lang"); // bỏ qua

        log.info("✅ Đã thêm AuthInterceptor và LocaleChangeInterceptor vào registry!");
    }
}
