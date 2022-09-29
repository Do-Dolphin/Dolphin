package com.dolphin.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor
public class RedisService {

        private final RedisTemplate<String, String> redisTemplate;

        //key, date 저장
        public void setValues(String key, String data) {
            ValueOperations<String, String> values = redisTemplate.opsForValue();
            values.set(key, data);
        }

         // 유효 시간 동안 (key, value) 저장
        public void setValuesExpire(String key, String data, Long duration) {
            ValueOperations<String, String> values = redisTemplate.opsForValue();
            Duration expireDuration = Duration.ofSeconds(duration);
            values.set(key, data, expireDuration);
        }

        //key를 통해 date 리턴
        public String getValues(String key) {
            ValueOperations<String, String> values = redisTemplate.opsForValue();
            return values.get(key);
        }

        // 유효 시간 동안 (key, value) 저장
        public String setExpire(String key, Long timeout, TimeUnit unit) {
            ValueOperations<String, String> values = redisTemplate.opsForValue();
            return values.getAndExpire(key, timeout, unit);
        }

        //key로 해당 date 삭제
        public void deleteValues(String key) {
            redisTemplate.delete(key);
        }

}
