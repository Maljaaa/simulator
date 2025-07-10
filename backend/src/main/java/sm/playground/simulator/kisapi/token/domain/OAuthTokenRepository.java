package sm.playground.simulator.kisapi.token.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface OAuthTokenRepository extends JpaRepository<OAuthToken, Long> {
    @Query("""
        SELECT t 
        FROM OAuthToken t
        WHERE t.provider = :provider
            AND t.accessTokenTokenExpired > CURRENT_TIMESTAMP        
    """)
    Optional<OAuthToken> findValidToken(String provider);
}
