package com.url.shortener.scheduler;

import com.url.shortener.operation.URLRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@EnableScheduling
@RequiredArgsConstructor
public class ClickSyncScheduler {
    private final RedisTemplate<String, String> redisTemplate;
    private final URLRepository urlRepository;

    @Scheduled(fixedRate = 60000)
    public void syncToDb(){
        Set<String> keys = redisTemplate.keys("clicks:*");
        for(String key : keys){
            String shortCode = key.split(":")[1];
            String val = redisTemplate.opsForValue().get(key);
            if(val!=null){
                Long count = Long.parseLong(val);
                urlRepository.updateClickCount(shortCode, count);

                // Reset Redis counter after syncing
                redisTemplate.delete(key);
            }
        }
    }
}
