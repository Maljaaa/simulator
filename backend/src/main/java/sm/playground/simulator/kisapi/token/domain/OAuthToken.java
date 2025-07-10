package sm.playground.simulator.kisapi.token.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sm.playground.simulator.kisapi.token.dto.OAuthTokenResponse;
import sm.playground.simulator.kisapi.token.security.AesCbcEncryptor;

import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.*;

@Entity
@Table(name = "oauth_token")
@Getter
@NoArgsConstructor
public class OAuthToken {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    /*
        토큰 발급 기관: KIS(한국투자증권)
     */
    @Column(nullable = false, length = 50)
    private String provider;

    /*
        OAuth 토큰이 필요한 API 경우 발급한 Access token
        ex) "eyJ0eXUxMiJ9.eyJz…..................................."

        - 일반개인고객/일반법인고객
        . Access token 유효기간 1일
        .. 일정시간(6시간) 이내에 재호출 시에는 직전 토큰값을 리턴
        . OAuth 2.0의 Client Credentials Grant 절차를 준용

        - 제휴법인
        . Access token 유효기간 3개월
        . Refresh token 유효기간 1년
        . OAuth 2.0의 Authorization Code Grant 절차를 준용
     */
    @Column(name = "access_token", nullable = false, length = 350)
    private String encryptedAccessToken;

    @Transient
    private String accessToken;

    public void encryptAccessToken(AesCbcEncryptor encryptor) {
        this.encryptedAccessToken = encryptor.encrypt(this.accessToken);
    }

    public void decryptAccessToken(AesCbcEncryptor encryptor) {
        this.accessToken = encryptor.decrypt(this.encryptedAccessToken);
    }

    /*
        접근토큰유형 : "Bearer"
        ※ API 호출 시, 접근토큰유형 "Bearer" 입력. ex) "Bearer eyJ...."
     */
    @Column(name = "token_type", nullable = false, length = 20)
    private String tokenType;

    /*
        유효기간(초)
e       x) 7776000
     */
    @Column(name = "expires_in", nullable = false)
    private Integer expiresIn;

    /*
        유효기간(년:월:일 시:분:초)
        ex) "2022-08-30 08:10:10"
     */
    @Column(name = "access_token_token_expired", nullable = false)
    private LocalDateTime accessTokenTokenExpired;

    public OAuthToken(String provider, String accessToken, String tokenType, Integer expiresIn, LocalDateTime accessTokenTokenExpired) {
        this.provider = provider;
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
        this.accessTokenTokenExpired = accessTokenTokenExpired;
    }

    // == 비즈니스 메서드 ==
    public boolean isValid() {
        return accessTokenTokenExpired.isAfter(LocalDateTime.now());
    }

    public void updateFrom(OAuthTokenResponse dto, AesCbcEncryptor encryptor) {
        this.accessToken = dto.accessToken();
        this.tokenType = dto.tokenType();
        this.expiresIn = dto.expiresIn();
        this.accessTokenTokenExpired = dto.accessTokenTokenExpired();
        this.encryptAccessToken(encryptor);
    }

}
