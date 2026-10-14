package lk.sliit.hotel.payment.config;

import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import java.time.Clock;
import java.time.ZoneId;

@Configuration
@EnableMethodSecurity
public class PaymentConfiguration {
    @Bean public Clock paymentClock() { return Clock.system(ZoneId.of("Asia/Colombo")); }
}
