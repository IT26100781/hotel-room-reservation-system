package lk.sliit.hotel.payment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.Map;

@RestController
public class SessionController {
    @GetMapping("/") public ResponseEntity<Void> home() {
        return ResponseEntity.status(302).location(URI.create("/staff/payments/index.html")).build();
    }
    @GetMapping("/api/csrf") public Map<String, String> csrf(CsrfToken token) {
        return Map.of("headerName", token.getHeaderName(), "token", token.getToken());
    }
    @GetMapping("/api/session") public Map<String, Object> session(Authentication auth) {
        return Map.of("username", auth.getName(), "roles", auth.getAuthorities().stream().map(Object::toString).toList());
    }
}
