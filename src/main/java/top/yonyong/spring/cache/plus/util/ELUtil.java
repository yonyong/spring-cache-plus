package top.yonyong.spring.cache.plus.util;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * @author  yonyong
 * @since   1.0.0
 */
public class ELUtil {
    private static final ExpressionParser parser = new SpelExpressionParser();
    private static final String spElFlag = "#";

    public static StandardEvaluationContext getContext(){
        return new StandardEvaluationContext();
    }

    /**
     * 判断el condition是否为true
     * @param el el condition
     * @param context EvaluationContext
     * @return true条件成立 / false条件不成立
     */
    public static boolean parseCondition(String el, EvaluationContext context){
        String condition = parse(el, context);
        return Boolean.parseBoolean(condition);
    }

    /**
     * EL解析 返回String类型的值
     * @param el el表达式
     * @param context EvaluationContext
     * @return 解析后的String类型的值
     */
    public static String parse(String el, EvaluationContext context){
        String value;
        // 如果包含#字符，则使用SpringEL表达式进行解析
        if (el.contains(spElFlag)) {
            value = resolveValueByExpression(el, context);
        } else {
            // 否则不处理
            value = el;
        }
        return value;
    }

    private static String resolveValueByExpression(String spELString, EvaluationContext context) {
        // 构建表达式
        Expression expression = parser.parseExpression(spELString);
        // 解析
        return expression.getValue(context, String.class);
    }
}
