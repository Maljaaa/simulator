package sm.playground.simulator.kisapi.token.service;

import jakarta.transaction.Transactional;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import sm.playground.simulator.kisapi.token.config.KoreaInvestProperties;
import sm.playground.simulator.kisapi.token.domain.OAuthToken;
import sm.playground.simulator.kisapi.token.domain.OAuthTokenRepository;
import sm.playground.simulator.kisapi.token.dto.OAuthTokenResponse;
import sm.playground.simulator.kisapi.token.security.AesCbcEncryptor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

@Service
public class TokenService {

    private final KoreaInvestProperties properties;
    private final OAuthTokenRepository tokenRepository;
    private final AesCbcEncryptor encryptor;
    private final RedisTokenCacheService cacheService;
    private final RestClient restClient;


    private final String INVESTMENT_KIS = "KIS";

    public TokenService(KoreaInvestProperties properties,
                        OAuthTokenRepository tokenRepository, RedisTokenCacheService cacheService,
                        AesCbcEncryptor encryptor) {
        this.properties = properties;
        this.tokenRepository = tokenRepository;
        this.cacheService = cacheService;
        this.encryptor = encryptor;
        this.restClient = RestClient.builder()
                .baseUrl(properties.baseUrl())
                .build();
    }

    @Transactional
    public String getValidAccessToken() {
        String cached = cacheService.get(INVESTMENT_KIS);
        if (cached != null) return cached;
        // 기존 유효한 토큰이 있는지 확인
        return tokenRepository.findValidToken(INVESTMENT_KIS)
                .map(existingToken -> {
                    // ✅ 아직 유효하다면 복호화하여 그대로 사용
                    if (existingToken.getAccessTokenTokenExpired().isAfter(LocalDateTime.now())) {
                        existingToken.decryptAccessToken(encryptor);
                        String decrypted = existingToken.getAccessToken();
                        cacheService.save(INVESTMENT_KIS, decrypted, existingToken.getAccessTokenTokenExpired());
                        return decrypted;
                    }

                    // ✅ 만료되었으면 새로 발급 후 업데이트
                    OAuthTokenResponse refreshed = requestToken();
                    existingToken.updateFrom(refreshed, encryptor);
                    tokenRepository.save(existingToken);
                    cacheService.save(INVESTMENT_KIS, refreshed.accessToken(), refreshed.accessTokenTokenExpired());
                    return refreshed.accessToken();
                })
                .orElseGet(() -> {
                    // ✅ DB에 토큰이 아예 없다면 새로 발급 후 저장
                    OAuthTokenResponse newToken = requestToken();
                    OAuthToken token = new OAuthToken(
                            INVESTMENT_KIS,
                            newToken.accessToken(),
                            newToken.tokenType(),
                            newToken.expiresIn(),
                            newToken.accessTokenTokenExpired()
                    );
                    token.encryptAccessToken(encryptor);
                    tokenRepository.save(token);
                    cacheService.save(INVESTMENT_KIS, newToken.accessToken(), newToken.accessTokenTokenExpired());
                    return newToken.accessToken();
                });
    }

    public OAuthTokenResponse requestToken() {
        Map<String, String> requestBody = Map.of(
                "grant_type", properties.grantType(),
                "appkey", properties.appKey(),
                "appsecret", properties.appSecret()
        );

        Map<String, Object> response = restClient.post()
                .uri("https://openapi.koreainvestment.com:9443/oauth2/tokenP")
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .body(Map.class);

        String accessToken = (String) Objects.requireNonNull(response).get("access_token");
        String tokenType = (String) response.get("token_type");
        Integer expiresIn = (Integer) response.get("expires_in");
        LocalDateTime accessTokenTokenExpired = LocalDateTime.now().plusSeconds(expiresIn);

        return new OAuthTokenResponse(
                accessToken,
                tokenType,
                expiresIn,
                accessTokenTokenExpired
        );
    }
}
