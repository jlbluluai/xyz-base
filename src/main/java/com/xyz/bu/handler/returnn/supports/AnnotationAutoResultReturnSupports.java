package com.xyz.bu.handler.returnn.supports;

import com.xyz.bu.handler.returnn.AutoResult;
import com.xyz.bu.handler.returnn.NotAutoResult;
import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * @author xyz
 * @date 2020/7/17
 */
public class AnnotationAutoResultReturnSupports extends AutoResultReturnSupports {

    @Override
    public boolean supportsReturnType(@NonNull MethodParameter methodParameter, @Nullable String param) {
        // 注解模式也可适用NotAutoResult
        // 比如类用了 NotAutoResult，方法用了 AutoResult
        // 本着大级别优先原则 方法也会是 NotAutoResult
        if (methodParameter.getMethodAnnotation(NotAutoResult.class) != null
                || methodParameter.getDeclaringClass().getAnnotation(NotAutoResult.class) != null) {
            return false;
        }

        return methodParameter.getMethodAnnotation(AutoResult.class) != null
                || methodParameter.getDeclaringClass().getAnnotation(AutoResult.class) != null;
    }

}
