package com.salapp.ticket.authserver.config;

import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TotpConfig {
    @Bean
    public SecretGenerator secretGenerator() {
        return new DefaultSecretGenerator(); // Generates 32-character secrets by default
    }

    @Bean
    public QrGenerator qrGenerator() {
        return new ZxingPngQrGenerator(); // Uses ZXing to generate PNG QR codes
    }

    @Bean
    public TimeProvider timeProvider() {
        return new SystemTimeProvider(); // Uses system time for TOTP
    }

    @Bean
    public CodeVerifier codeVerifier() {
        return new DefaultCodeVerifier(new DefaultCodeGenerator(), timeProvider()); // Verifies TOTP codes
    }
}
