package top.yonyong.spring.cache.plus.client;

import java.util.concurrent.TimeUnit;

/**
 * @author  yonyong
 * @since   1.0.0
 */
public interface YangCacheTemplate {

    boolean exist(String key);

    boolean set(String key, Object Value, long time, TimeUnit timeUnit);

    boolean del(String key);

    Object get(String key);
}
