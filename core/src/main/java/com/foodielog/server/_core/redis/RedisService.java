package com.foodielog.server._core.redis;

import com.foodielog.server._core.error.exception.Exception500;
import com.foodielog.server._core.util.JsonConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class RedisService {
    private final RedisTemplate<String, String> redisTemplate;
    private final JsonConverter jsonConverter;

    public <T> T getObjectByKey(String key, Class<T> clazz) {
        ValueOperations<String, String> vop = redisTemplate.opsForValue();
        String jsonString = vop.get(key);

        if (jsonString == null || jsonString.isEmpty()) {
            log.debug("redis get 오류");
            throw new Exception500("서버 오류! #R");
        }

        T object = jsonConverter.jsonToObject(jsonString, clazz);
        log.info("redis get 성공: " + object.toString());
        return object;
    }

    public void setObjectByKey(String key, Object obj, Long timeOut, TimeUnit timeUnit) {
        ValueOperations<String, String> vop = redisTemplate.opsForValue();
        vop.set(key, jsonConverter.objectToJson(obj));
        redisTemplate.expire(key, timeOut, timeUnit);
        log.info("redis set 성공 key: " + key);
    }
}
