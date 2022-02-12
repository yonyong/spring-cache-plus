package top.yonyong.spring.cache.plus.aspect;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.Ordered;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import top.yonyong.spring.cache.plus.annotation.CacheDel;
import top.yonyong.spring.cache.plus.annotation.CacheGet;
import top.yonyong.spring.cache.plus.annotation.CacheSet;
import top.yonyong.spring.cache.plus.client.YangCacheTemplate;
import top.yonyong.spring.cache.plus.util.AspectUtil;
import top.yonyong.spring.cache.plus.util.ELUtil;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author  yonyong
 * @since   1.0.0
 */
@Slf4j
@Aspect
@Component
@AllArgsConstructor
@ConditionalOnProperty(prefix = "system.cache.aop", name = "enable", havingValue = "true", matchIfMissing = true)
public class CachePointcut implements Ordered {

    private final YangCacheTemplate cacheTemplate;

    @Pointcut(value = "@annotation(top.yonyong.spring.cache.plus.annotation.CacheGet)")
    private void cacheGet() {
    }

    @Pointcut(value = "@annotation(top.yonyong.spring.cache.plus.annotation.CacheSet)")
    private void cacheSet() {
    }

    @Pointcut(value = "@annotation(top.yonyong.spring.cache.plus.annotation.CacheDel),")
    private void cacheDel() {
    }

    @Around("cacheGet()")
    public Object handleCacheGet(ProceedingJoinPoint point) throws Throwable {
        CacheGet cacheGet = AspectUtil.getAnnotation(point, CacheGet.class);
        String condition = cacheGet.condition();

        StandardEvaluationContext context = buildContext(point);
        if (ELUtil.parseCondition(condition, context)) {
            // 从缓存中 获取
            String key = cacheGet.key();
            //缓存key = 前缀 + key
            String cacheKey = cacheGet.prefix() + ELUtil.parse(key, context);
            if (cacheTemplate.exist(cacheKey)) {
                return cacheTemplate.get(cacheKey);
            } else if (cacheGet.selectIfNull()) {
                //查询缓存失败下需要继续查询持久层
                Object result = point.proceed();
                if (null != result && cacheGet.setIfNull()) {
                    //持久层存在数据，需要同步数据至缓存中
                    cacheTemplate.set(cacheKey, result, cacheGet.timeout(), cacheGet.timeunit());
                }
                return result;
            } else {
                return null;
            }
        } else {
            // 从持久层获取
            return point.proceed();
        }
    }

    @Around("cacheSet()")
    public Object handleCachePet(ProceedingJoinPoint point) throws Throwable {
        CacheSet cacheSet = AspectUtil.getAnnotation(point, CacheSet.class);
        Object result = point.proceed();

        String condition = cacheSet.condition();
        StandardEvaluationContext context = buildContext(point);
        if (ELUtil.parseCondition(condition, context) && null != result) {
            // 从缓存中 获取
            String key = cacheSet.key();
            //缓存key = 前缀 + key
            String cacheKey = cacheSet.prefix() + ELUtil.parse(key, context);
            cacheTemplate.set(cacheKey, result, cacheSet.timeout(), cacheSet.timeunit());
        }
        return point.proceed();
    }

    @Around("cacheDel()")
    public Object handleCacheDel(ProceedingJoinPoint point) throws Throwable {
        CacheDel cachePut = AspectUtil.getAnnotation(point, CacheDel.class);
        String condition = cachePut.condition();
        StandardEvaluationContext context = buildContext(point);
        if (ELUtil.parseCondition(condition, context)) {
            // 从缓存中 获取
            String key = cachePut.key();
            //缓存key = 前缀 + key
            String cacheKey = cachePut.prefix() + ELUtil.parse(key, context);
            if (cacheTemplate.exist(cacheKey)) {
                cacheTemplate.del(cacheKey);
            }
        }
        return point.proceed();
    }

    @Override
    public int getOrder() {
        return 1;
    }

    private StandardEvaluationContext buildContext(ProceedingJoinPoint point) {
        // 获取方法入参，key为参数名，value为参数值
        LinkedHashMap<String, Object> params = AspectUtil.getParams(point);
        // 求值上下文
        StandardEvaluationContext context = ELUtil.getContext();
        if (params.size() == 1) {
            // 当参数只有一个时，设置根对象，例如入参为对象，则此时可以使用 #root.id 来获取对象的id
            params.forEach((k, v) -> context.setRootObject(v));
        }
        int i = 0;
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            context.setVariable(entry.getKey(), entry.getValue());
            // 设置参数别名，按顺序，可使用 #a0 或 #p0 来获取第一个入参
            context.setVariable("a" + i, entry.getValue());
            context.setVariable("p" + i, entry.getValue());
            i++;
        }
        return context;
    }
}
