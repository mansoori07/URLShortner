package com.url.shortener.operation;

import io.github.bucket4j.Bucket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.Duration;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class URLController {

    private final URLService urlService;

    private final Bucket bucket = Bucket.builder()
            .addLimit(limit ->
                    limit.capacity(10).refillGreedy(10, Duration.ofMinutes(1))
            )
            .build();

    @PostMapping("/api/shorten")
    public ResponseEntity<?> create(@RequestBody String url) {
        if (!bucket.tryConsume(1)) return ResponseEntity.status(429).body("Too many requests");
        log.info("URL : {}", url);
        return ResponseEntity.ok(Map.of("shortUrl", "http://localhost:8080/" + urlService.shortenUrl(url)));
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode) {
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(urlService.getLongUrl(shortCode)))
                .build();
    }
}
