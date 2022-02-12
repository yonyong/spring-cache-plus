EN| [中文](https://github.com/yonyong/spring-cache-plus/blob/master/README.md)

# spring-cache-plus - More flexible caching components

## 一、Basic

### 1. Introduction

> spring cache component is not very friendly to the timeout settings, so the pain point, borrowed from the spring cache ideas to redesign a more flexible caching components.
>
> The **default cache store uses redis**, but can also be configured to access other caching frameworks.

### 2. Usage Scenarios
> Similar to the spring cache component, the cache is manipulated in an annotated manner.

### 3. Advantages
> 1. support for setting the cache expiry time.
> 2. use annotations to manipulate the cache, simple usage.
> 3. based on spring ecology out of the box, fast to start, minimize the invasiveness of the code.
> 4. flexible and open caching framework, support for various caching frameworks minimal access (default use redis cache).

## 二、Quick start


### 1.Introduction of dependencies
```xml
<dependency>
  <groupId>top.yonyong</groupId>
  <artifactId>spring-cache-plus</artifactId>
  <version>1.0.0</version>
</dependency>
```
**Friendly tips**：

redis and AOP dependencies **Not required** Add, this framework has default access to dependencies on：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

### 2.Configuring redis information

```properties
# The default configuration of spring redis is sufficient
spring.redis.host=127.0.0.1
spring.redis.port=6379
spring.redis.password=123456
spring.redis.database=0
spring.redis.timeout=3
```

### 3.  Using annotations to manipulate the cache

```java
@Repository
public class DaoImpl implements Dao{

    @Override
    @CacheGet(key = "#name",timeout = 60)
    public String get(String name) {
        return "get" + name;
    }

    @Override
    @CacheSet(key = "#user.name",timeout = 6,timeunit = TimeUnit.MINUTES)
    public User setUser(User user) {
        user.setId("SET");
        return user;
    }

    @Override
    @CacheDel(key = "#user.name")
    public List<User> delList(User user) {
        return null;
    }

}
```

#### CacheGet, CacheSet and CacheDel annotated attributes.：

##### CacheGet

| Property name | Type of property | Default value    | Property Remarks                                             |
| ------------- | ---------------- | ---------------- | ------------------------------------------------------------ |
| prefix        | String           | ""               | Key name prefixes that end up in the cache                   |
| key           | String           | ""               | Key name suffixes that are eventually stored in the cache (EL syntax supported) |
| condition     | String           | ""               | Cache-related operations are performed only when conditions are met (EL syntax supported) |
| selectIfNull  | boolean          | true             | Do you need to query from the persistence layer when the cache query is not available |
| setIfNull     | boolean          | true             | If data exists in the persistence layer in a cache query scenario, does it need to be additionally stored in the cache |
| timeout       | long             | -1               | Cache timeout/expiration time                                |
| timeunit      | TimeUnit         | TimeUnit.SECONDS | Cache timeout unit Default seconds                           |

##### CacheSet

| Property name | Type of property | Default value    | Property Remarks                                             |
| ------------- | ---------------- | ---------------- | ------------------------------------------------------------ |
| prefix        | String           | ""               | Key name prefixes that end up in the cache                   |
| key           | String           | ""               | Key name suffixes that are eventually stored in the cache (EL syntax supported) |
| condition     | String           | ""               | Cache-related operations are performed only when conditions are met (EL syntax supported) |
| timeout       | long             | -1               | Cache timeout/expiration time                                |
| timeunit      | TimeUnit         | TimeUnit.SECONDS | Cache timeout unit Default seconds                           |

##### CacheDel

| Property name | Type of property | Default value | Property Remarks                                             |
| ------------- | ---------------- | ------------- | ------------------------------------------------------------ |
| prefix        | String           | ""            | Key name prefixes that end up in the cache                   |
| key           | String           | ""            | Key name suffixes that are eventually stored in the cache (EL syntax supported) |
| condition     | String           | ""            | Cache-related operations are performed only when conditions are met (EL syntax supported) |


## 三、Custom configuration

### 1. Customizing redis configuration

Support for custom redis configuration

#### 1.1 Disable the default redis configuration

Configuration file to add configuration

```properties
# application.properties redis configuration switches
system.cache.client.redis.enable=false
```

#### 1.2 Custom redis configuration

```java
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> init(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        // Custom configuration ......
        return redisTemplate;
    }
}
```

#### 1.3 实现YangCacheTemplate

```java
@Component
@AllArgsConstructor、
public class RedisCacheTemplate implements YangCacheTemplate {

    //Reference code, logic self-implemented
    private final RedisTemplate<String,Object> yangRedisTemplate;

    @Override
    public boolean exist(String key) {
        //Reference code, logic self-implemented
        return yangRedisTemplate.hasKey(key);
    }

    @Override
    public boolean set(String key, Object value, long time, TimeUnit timeUnit) {
        //Reference code, logic self-implemented
        yangRedisTemplate.opsForValue().set(key, value, time, timeUnit);
        return false;
    }

    @Override
    public boolean del(String key) {
        //Reference code, logic self-implemented
        yangRedisTemplate.delete(key);
        return false;
    }

    @Override
    public Object get(String key) {
        //Reference code, logic self-implemented
        return yangRedisTemplate.opsForValue().get(key);
    }
}
```



### 2. Replacing redis with other caching frameworks

#### 1.1 Disabling redis

Configuration file to add configuration

```properties
# application.properties redis configuration switches
system.cache.client.redis.enable=false
```

#### 1.2 Implementation of YangCacheTemplate

```java
@Component
@AllArgsConstructor
@ConditionalOnProperty(prefix = "system.cache.client.redis", name = "enable", havingValue = "true", matchIfMissing = true)
public class RedisCacheTemplate implements YangCacheTemplate {

    private final Third-partyClient client;

    @Override
    public boolean exist(String key) {
        
        // client.exist ......
    }

    @Override
    public boolean set(String key, Object value, long time, TimeUnit timeUnit) {
        // Set cache set k,v
        // client.set ......
    }

    @Override
    public boolean del(String key) {
        // Delete cache del k
        // client.del ......
    }

    @Override
    public Object get(String key) {
        // Query cache get k
        // client.del ......
    }
}
```



## Contact me

> Github： https://github.com/yonyong
>
> Wechat：young2365878736
