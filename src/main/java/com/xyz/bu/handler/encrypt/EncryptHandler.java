package com.xyz.bu.handler.encrypt;

import com.xyz.bu.exception.BusinessException;
import com.xyz.bu.handler.returnn.AutoResultReturnHandler;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import java.lang.reflect.Field;

/**
 * 加密/解密处理器
 * <p>
 * 采用装饰器模式对原本的处理器进行扩展，并结合 {@link RequestMappingHandlerAdapter} 进行覆盖
 * 配置见 {@link BaseWebMvcConfig}
 * <p>
 * 参数解析器暂只支持 {@link RequestBody} 的方式
 * <p>
 * 返回值处理器暂只支持 {@link AutoResultReturnHandler} 的方式
 *
 * @author xyz
 * @date 2021/10/25
 **/
public class EncryptHandler implements HandlerMethodArgumentResolver, HandlerMethodReturnValueHandler {

    /**
     * 用来装饰的参数解析器
     */
    private HandlerMethodArgumentResolver argumentResolver;

    /**
     * 用来装饰的返回值处理器
     */
    private HandlerMethodReturnValueHandler returnValueHandler;

    /**
     * 加密/解密算法
     */
    private EncryptInterface encryptInterface;

    public EncryptHandler(HandlerMethodArgumentResolver argumentResolver, EncryptInterface encryptInterface) {
        if (!(argumentResolver instanceof RequestResponseBodyMethodProcessor)) {
            throw new BusinessException("不支持该参数解析器的装饰 classType-" + argumentResolver.getClass().getSimpleName());
        }
        this.argumentResolver = argumentResolver;
        this.encryptInterface = encryptInterface;
    }

    public EncryptHandler(HandlerMethodReturnValueHandler returnValueHandler, EncryptInterface encryptInterface) {
        if (!(returnValueHandler instanceof AutoResultReturnHandler)) {
            throw new BusinessException("不支持该返回值处理器的装饰 classType-" + returnValueHandler.getClass().getSimpleName());
        }
        this.returnValueHandler = returnValueHandler;
        this.encryptInterface = encryptInterface;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return argumentResolver.supportsParameter(parameter);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Object argument = argumentResolver.resolveArgument(parameter, mavContainer, webRequest, binderFactory);
        if (argument instanceof EncryptPojo) {
            Field[] fields = argument.getClass().getDeclaredFields();
            for (Field field : fields) {
                EncryptField encryptField = field.getDeclaredAnnotation(EncryptField.class);
                if (encryptField != null && field.getType() == String.class) {
                    field.setAccessible(true);
                    String content = (String) field.get(argument);
                    String decrypt = encryptInterface.decrypt(content);
                    field.set(argument, decrypt);
                }
            }
        }
        return argument;
    }

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return returnValueHandler.supportsReturnType(returnType);
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        if (returnValue instanceof EncryptPojo) {
            Field[] fields = returnValue.getClass().getDeclaredFields();
            for (Field field : fields) {
                EncryptField encryptField = field.getDeclaredAnnotation(EncryptField.class);
                if (encryptField != null && field.getType() == String.class) {
                    field.setAccessible(true);
                    String content = (String) field.get(returnValue);
                    String encrypt = encryptInterface.encrypt(content);
                    field.set(returnValue, encrypt);
                }
            }
        }
        returnValueHandler.handleReturnValue(returnValue, returnType, mavContainer, webRequest);
    }

}
