package top.yonyong.spring.cache.plus.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * @author  yonyong
 * @since   1.0.0
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface CacheSet {

 /**
  * redis 键名前缀
  * @return 键名前缀
  */
 String prefix() default "";

 /**
  * redis 键名
  * @return 键名
  */
 String key() default "";

 /**
  * 满足条件会先从redis中操作
  * @return 条件
  */
 String condition() default "true";

 /**
  * 设置超时时间
  * @return 超时时间(默认永久)
  */
 long timeout() default -1;

 /**
  * 时间单位 默认秒
  * @return 时间单位
  */
 TimeUnit timeunit() default TimeUnit.SECONDS;
}