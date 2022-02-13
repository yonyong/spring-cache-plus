*_[EN](https://gitee.com/yonyong/spring-cache-plus/blob/master/README_EN.md) | 中文_*

# spring-cache-plus - 更灵活的缓存组件

## 一、引言

### 1. 介绍

> spring cache组件对超时设置不太友善，由此痛点，借鉴spring cache的思路重新设计了一套更灵活的缓存组件。
>
> **默认缓存存储使用redis**，也可以通过简单配置接入其他缓存框架。

### 2. 使用场景
> 与spring cache组件类似，以注解方式来操作缓存。

### 3. 优势
> 1.支持设置缓存失效时长；
> 2.使用注解操作缓存，用法简单；
> 3.基于spring生态开箱即用，疾速上手，最大程度降低代码的侵入性；
> 4.缓存框架灵活开放，支持各缓存框架极简接入（默认使用redis缓存）。

## 二、快速入门


### 1.引入依赖
```xml
<dependency>
  <groupId>top.yonyong</groupId>
  <artifactId>spring-cache-plus</artifactId>
  <version>1.0.1</version>
</dependency>
```
**友情提示**：

redis以及AOP依赖  **非必须** 添加，本框架已默认接入依赖：

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

### 2.配置redis信息

```properties
# spring redis默认配置即可
spring.redis.host=127.0.0.1
spring.redis.port=6379
spring.redis.password=123456
spring.redis.database=0
spring.redis.timeout=3
```

### 3. 使用注解操作缓存

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

#### CacheGet、CacheSet及CacheDel注解属性：

##### CacheGet

| 属性名称     | 属性类型 | 默认值           | 属性备注                                                     |
| ------------ | -------- | ---------------- | ------------------------------------------------------------ |
| prefix       | String   | ""               | 最终存到缓存中的键名前缀                                     |
| key          | String   | ""               | 最终存到缓存中的键名后缀（支持EL语法）                       |
| condition    | String   | ""               | 当满足条件才会执行缓存相关操作（支持EL语法）                 |
| selectIfNull | boolean  | true             | 缓存查询不到时，是否需要从持久层查询                         |
| setIfNull    | boolean  | true             | 缓存查询不到时场景下，若持久层存在数据，是否需要额外存储到缓存中 |
| timeout      | long     | -1               | 缓存超时/失效时间                                            |
| timeunit     | TimeUnit | TimeUnit.SECONDS | 缓存超时时间单位 默认秒                                      |

##### CacheSet

| 属性名称  | 属性类型 | 默认值           | 属性备注                                     |
| --------- | -------- | ---------------- | -------------------------------------------- |
| prefix    | String   | ""               | 最终存到缓存中的键名前缀                     |
| key       | String   | ""               | 最终存到缓存中的键名后缀（支持EL语法）       |
| condition | String   | ""               | 当满足条件才会执行缓存相关操作（支持EL语法） |
| timeout   | long     | -1               | 缓存超时/失效时间                            |
| timeunit  | TimeUnit | TimeUnit.SECONDS | 缓存超时时间单位 默认秒                      |

##### CacheDel

| 属性名称  | 属性类型 | 默认值 | 属性备注                                     |
| --------- | -------- | ------ | -------------------------------------------- |
| prefix    | String   | ""     | 最终存到缓存中的键名前缀                     |
| key       | String   | ""     | 最终存到缓存中的键名后缀（支持EL语法）       |
| condition | String   | ""     | 当满足条件才会执行缓存相关操作（支持EL语法） |


## 三、自定义配置

### 1. 自定义redisTemplate 配置

支持自定义redisTemplate配置

#### 1.1 排除默认redisTemlate配置类

```java
@SpringBootApplication(exclude = RedisCacheConfig.class)
```

#### 1.2 自定义redis配置

```java
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> init(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        // 自定义配置 ......
        return redisTemplate;
    }
}
```

### 2. 自行实现redis 操作API

#### 2.1 排除RedisCacheTemplate

```java
@SpringBootApplication(exclude = RedisCacheConfig.class)
```

#### 2.2 实现YangCacheTemplate

```java
@Component
@AllArgsConstructor
@ConditionalOnProperty(prefix = "system.cache.client.redis", name = "enable", havingValue = "true", matchIfMissing = true)
public class RedisCacheTemplate implements YangCacheTemplate {

    private final RedisTemplate client;

    @Override
    public boolean exist(String key) {
        
        // client.exist ......
    }

    @Override
    public boolean set(String key, Object value, long time, TimeUnit timeUnit) {
        // 设置缓存 set k,v
        // client.set ......
    }

    @Override
    public boolean del(String key) {
        // 删除缓存 del k
        // client.del ......
    }

    @Override
    public Object get(String key) {
        // 查询缓存 get k
        // client.del ......
    }
}
```



### 3. 替换redis为其他缓存框架

#### 3.1 禁用redis

配置文件添加配置

```properties
# application.properties redis配置开关
system.cache.client.redis.enable=false
```

#### 3.2 实现 YangCacheTemplate

```java
@Component
@AllArgsConstructor
@ConditionalOnProperty(prefix = "system.cache.client.redis", name = "enable", havingValue = "true", matchIfMissing = true)
public class RedisCacheTemplate implements YangCacheTemplate {

    private final 第三方缓存框架Client client;

    @Override
    public boolean exist(String key) {
        
        // client.exist ......
    }

    @Override
    public boolean set(String key, Object value, long time, TimeUnit timeUnit) {
        // 设置缓存 set k,v
        // client.set ......
    }

    @Override
    public boolean del(String key) {
        // 删除缓存 del k
        // client.del ......
    }

    @Override
    public Object get(String key) {
        // 查询缓存 get k
        // client.del ......
    }
}
```



## 联系我

> Github： https://github.com/yonyong
>
> Wechat：young2365878736
