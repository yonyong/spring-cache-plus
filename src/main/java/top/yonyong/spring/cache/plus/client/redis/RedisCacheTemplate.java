package top.yonyong.spring.cache.plus.client.redis;

import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import top.yonyong.spring.cache.plus.client.YangCacheTemplate;

import java.util.concurrent.TimeUnit;

/**
 * @author  yonyong
 * @since   1.0.0
 */
@Component
@AllArgsConstructor
@ConditionalOnProperty(prefix = "system.cache.client.redis", name = "enable", havingValue = "true", matchIfMissing = true)
public class RedisCacheTemplate implements YangCacheTemplate {

    private final RedisTemplate<String,Object> yangRedisTemplate;

    @Override
    public boolean exist(String key) {
        return yangRedisTemplate.hasKey(key);
    }

    @Override
    public boolean set(String key, Object value, long time, TimeUnit timeUnit) {
        System.out.println(String.format("【添加缓存】key：%s,value:%s,time:%s,timeUnit:%s", key, value, time, timeUnit));
        yangRedisTemplate.opsForValue().set(key, value, time, timeUnit);
        return false;
    }

    @Override
    public boolean del(String key) {
        System.out.println(String.format("【删除缓存】key：%s", key));
        yangRedisTemplate.delete(key);
        return false;
    }

    @Override
    public Object get(String key) {
        System.out.println(String.format("【查询缓存】key：%s", key));
        return yangRedisTemplate.opsForValue().get(key);
    }
}
