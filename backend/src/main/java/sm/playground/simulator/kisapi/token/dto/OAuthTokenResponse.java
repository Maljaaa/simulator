package sm.playground.simulator.kisapi.token.dto;

import java.time.LocalDateTime;

public record OAuthTokenResponse (
        String accessToken,
        String tokenType,
        Integer expiresIn,
        LocalDateTime accessTokenTokenExpired
) {}
