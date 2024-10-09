package com.webtoon.utils;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisUtils {
    RedisTemplate<String, String> redisTemplate0;
    RedisTemplate<String, Object> redisTemplate1;

    public String getDataTo0(String key) {
        return redisTemplate0.opsForValue().get(key);
    }

    public void setDataTo0(String key, String value) {
        redisTemplate0.opsForValue().set(key, value, 3600, TimeUnit.SECONDS);
    }

    public Object getDataTo1(String key) {
        return redisTemplate1.opsForValue().get(key);
    }

    public void setDataTo1(String key, Object value) {
        redisTemplate1.opsForValue().set(key, value, 600, TimeUnit.SECONDS);
    }
}
