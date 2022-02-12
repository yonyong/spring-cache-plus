package top.yonyong.spring.cache.plus.util;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;

/**
 * @author  yonyong
 * @since   1.0.0
 */
public class AspectUtil {

    /**
     * 获取注解实例
     * @param point ProceedingJoinPoint
     * @param annotationClass 注解类型
     * @param <T> 泛型
     * @return 注解实例
     * @throws NoSuchMethodException 异常
     */
    public static <T extends Annotation> T  getAnnotation(ProceedingJoinPoint point, Class<T> annotationClass) throws NoSuchMethodException {
        Object target = point.getTarget();
        Signature signature = point.getSignature();
        MethodSignature methodSignature = null;
        if (!(signature instanceof MethodSignature)) {
            throw new IllegalArgumentException("该注解只能用于方法");
        }
        methodSignature = (MethodSignature) signature;
        Method currentMethod = target.getClass().getMethod(methodSignature.getName(), methodSignature.getParameterTypes());
        return currentMethod.getDeclaredAnnotation(annotationClass);
    }

    /**
     * 获取方法入参参数
     * @param joinPoint ProceedingJoinPoint
     * @return 获取方法入参，key为参数名，value为参数值
     */
    public static LinkedHashMap<String, Object> getParams(ProceedingJoinPoint joinPoint){
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] arguments = joinPoint.getArgs();
        String[] paramNames = getParameterNames(method);

        LinkedHashMap<String, Object> params = new LinkedHashMap<>();
        for (int i = 0; i < arguments.length; i++) {
            params.put(paramNames[i], arguments[i]);
        }
        return params;
    }

    private static String[] getParameterNames(Method method) {
        ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
        return parameterNameDiscoverer.getParameterNames(method);
    }
}
