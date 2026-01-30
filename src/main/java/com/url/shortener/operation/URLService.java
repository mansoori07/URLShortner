package com.url.shortener.operation;

import com.url.shortener.models.URLMapping;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class URLService {

    private final URLRepository urlRepository;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public String shortenUrl(String longUrl) {
        URLMapping mapping = new URLMapping();
        mapping.setLongUrl(longUrl);
        mapping.setShortCode("TEMP");
        mapping = urlRepository.save(mapping);

        String shortCode = encode(mapping.getId());
        log.info("Short URL - {}", shortCode);
        mapping.setShortCode(shortCode);
        urlRepository.save(mapping);

        redisTemplate.opsForValue().set(shortCode, longUrl, Duration.ofMinutes(24));
        return shortCode;
    }

    public String getLongUrl(String shortCode) {

        // 1. Increment clicks in Redis (Atomic)
        redisTemplate.opsForValue().increment("clicks:"+shortCode);

        // 2. Cache Hit
        String cacheUrl =  redisTemplate.opsForValue().get(shortCode);
        if(cacheUrl != null)
            return cacheUrl;

        // 3. Cache Miss (DB)
        URLMapping mapping = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new RuntimeException("URL NOT FOUND"));

        redisTemplate.opsForValue().set(shortCode, mapping.getLongUrl(), Duration.ofMinutes(24));
        log.info("URL - {}", mapping.getLongUrl());
        return mapping.getLongUrl();
    }

    private String encode(Long id) {
        StringBuilder sb = new StringBuilder();
        while(id>0){
            sb.append(ALPHABET.charAt((int) (id%62)));
            id/=62;
        }
        return sb.reverse().toString();
    }
}
