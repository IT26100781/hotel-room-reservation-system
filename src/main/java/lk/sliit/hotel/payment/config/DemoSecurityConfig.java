package lk.sliit.hotel.payment.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

/** Demo login only. The staff team owns real users and staff.txt. */
@Configuration
@Profile("demo")
public class DemoSecurityConfig {
    @Bean public UserDetailsService demoUsers(@Value("${demo.password}") String password) {
        if (password.length() < 8 || password.length() > 72)
            throw new IllegalArgumentException("DEMO_STAFF_PASSWORD must be 8-72 characters");
        String hash = "{bcrypt}" + new BCryptPasswordEncoder().encode(password);
        return new InMemoryUserDetailsManager(
            User.withUsername("staff").password(hash).roles("STAFF").build(),
            User.withUsername("guest").password(hash).roles("GUEST").build());
    }
}
