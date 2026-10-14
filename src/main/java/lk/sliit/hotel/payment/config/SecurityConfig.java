package lk.sliit.hotel.payment.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import java.io.IOException;

/** Standalone web configuration. Merge these rules into the team's existing filter chain. */
@Configuration
public class SecurityConfig {
    @Bean public SecurityFilterChain paymentSecurity(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(a -> a
                .requestMatchers("/login.html", "/css/**", "/js/api.js", "/js/login.js", "/img/**",
                        "/api/csrf", "/error").permitAll()
                .requestMatchers("/api/session").authenticated()
                .anyRequest().hasAnyRole("STAFF", "ADMIN"))
            .formLogin(f -> f.loginPage("/login.html").loginProcessingUrl("/api/login")
                .successHandler((req, res, auth) -> json(res, 200, "Signed in"))
                .failureHandler((req, res, ex) -> json(res, 401, "Invalid username or password"))
                .permitAll())
            .logout(l -> l.logoutUrl("/api/logout")
                .logoutSuccessHandler((req, res, auth) -> json(res, 200, "Signed out")))
            // Default session-based CSRF protection stays enabled.
            .exceptionHandling(e -> e.authenticationEntryPoint((req, res, ex) -> {
                if (req.getRequestURI().startsWith("/api/")) json(res, 401, "Staff login required");
                else res.sendRedirect("/login.html");
            }).accessDeniedHandler((req, res, ex) -> json(res, 403, "Staff access and a valid CSRF token are required")));
        return http.build();
    }
    private static void json(HttpServletResponse res, int status, String message) throws IOException {
        res.setStatus(status); res.setContentType("application/json"); res.setCharacterEncoding("UTF-8");
        res.getWriter().write("{\"message\":\"" + message + "\"}");
    }
}
