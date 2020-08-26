package com.xyz.bu.handler.returnn.supports;

import com.xyz.bu.handler.returnn.AutoResult;
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
        return methodParameter.getMethodAnnotation(AutoResult.class) != null
                || methodParameter.getDeclaringClass().getAnnotation(AutoResult.class) != null;
    }

}
