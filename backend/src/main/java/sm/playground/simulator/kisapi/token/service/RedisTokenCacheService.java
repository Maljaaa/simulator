package sm.playground.simulator.kisapi.token.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

@Service
public class RedisTokenCacheService {
    private final RedisTemplate<String, String> redisTemplate;

    private static final String KEY_PREFIX = "oauth:token:";

    public RedisTokenCacheService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void save(String provider, String accessToken, LocalDateTime expireAt) {
        String key = buildKey(provider);
        long ttl = ChronoUnit.SECONDS.between(LocalDateTime.now(), expireAt);
        if (ttl > 0) {
            redisTemplate.opsForValue().set(key, accessToken, ttl, TimeUnit.SECONDS);
        }
    }

    public String get(String provider) {
        return redisTemplate.opsForValue().get(buildKey(provider));
    }

    public void delete(String provider) {
        redisTemplate.delete(buildKey(provider));
    }

    public boolean exists(String provider) {
        return redisTemplate.hasKey(buildKey(provider));
    }

    private String buildKey(String provider) {
        return KEY_PREFIX + provider + ":default";
    }
}
