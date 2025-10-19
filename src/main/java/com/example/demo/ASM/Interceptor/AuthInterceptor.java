package com.example.demo.ASM.Interceptor;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(AuthInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        String uri = request.getRequestURI();
        HttpSession session = request.getSession(false);

        // Bỏ qua các đường dẫn không cần chặn
        if (uri.startsWith("/login") || uri.startsWith("/logout") || uri.startsWith("/css")
                || uri.startsWith("/js") || uri.startsWith("/images") || uri.startsWith("/lang")) {
            return true;
        }

        // Kiểm tra đăng nhập
        if (session == null || session.getAttribute("user") == null) {
            log.warn("🚫 Chặn truy cập trái phép vào: {}", uri);
            response.sendRedirect("/login");
            return false;
        }

        // Log truy cập hợp lệ
        log.info("✅ Người dùng [{}] truy cập: {}", session.getAttribute("user"), uri);
        return true;
    }
}
