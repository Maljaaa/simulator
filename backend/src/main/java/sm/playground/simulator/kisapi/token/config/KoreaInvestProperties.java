package sm.playground.simulator.kisapi.token.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kisapi")
public record KoreaInvestProperties(
   String baseUrl,
   String appKey,
   String appSecret,
   String grantType
) {}
