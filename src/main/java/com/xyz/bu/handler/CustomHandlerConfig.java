package com.xyz.bu.handler;

import com.xyz.bu.exception.BusinessException;
import com.xyz.bu.handler.encrypt.DefaultEncryptService;
import com.xyz.bu.handler.encrypt.EncryptHandler;
import com.xyz.bu.handler.encrypt.EncryptInterface;
import com.xyz.bu.handler.encrypt.EncryptProperties;
import com.xyz.bu.handler.returnn.AutoResultReturnHandler;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import javax.annotation.Resource;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;

/**
 * 处理器配置
 * <p>
 * 实现InitializingBean的bean在启动时都会被执行afterPropertiesSet
 * 源码 {@link org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#invokeInitMethods}
 *
 * @author xyz
 * @date 2021/10/28
 **/
@Configuration
@EnableConfigurationProperties({EncryptProperties.class})
@Slf4j
public class CustomHandlerConfig implements InitializingBean {

    @Resource
    RequestMappingHandlerAdapter requestMappingHandlerAdapter;

    @Resource
    private EncryptProperties encryptProperties;

    @Resource
    private ApplicationContext applicationContext;

    private static final String ENCRYPT_ENABLE = "xyz.base.encrypt.enable";

    /**
     * 加密/解密服务
     * <p>
     * 默认不开启
     */
    @ConditionalOnProperty(value = ENCRYPT_ENABLE, havingValue = "true")
    @Bean
    public EncryptInterface encryptInterface(EncryptProperties encryptProperties) {
        if (!StringUtils.hasText(encryptProperties.getAlgorithm())) {
            return new DefaultEncryptService(encryptProperties.getAttribute());
        } else {
            try {
                Class<?> clazz = Class.forName(encryptProperties.getAlgorithm());
                Constructor<?>[] constructors = clazz.getDeclaredConstructors();
                Object o = null;
                for (Constructor<?> constructor : constructors) {
                    if (constructor.getParameterCount() == 0) {
                        o = constructor.newInstance();
                        break;
                    } else if (constructor.getParameterCount() == 1 && constructor.getParameterTypes()[0] == Map.class) {
                        o = constructor.newInstance(encryptProperties.getAttribute());
                        break;
                    }
                }
                if (o == null) {
                    throw new BusinessException("无法加载自定义的加密算法 未找到合适的构造函数");
                }
                if (!(o instanceof EncryptInterface)) {
                    throw new BusinessException("无法加载自定义的加密算法 未实现EncryptInterface");
                }
                return (EncryptInterface) o;
            } catch (ClassNotFoundException e) {
                throw new BusinessException("无法加载自定义的加密算法", e);
            } catch (BusinessException e) {
                throw e;
            } catch (Exception e) {
                throw new BusinessException("初始化自定义的加密算法失败", e);
            }
        }
    }

    @Override
    public void afterPropertiesSet() {
        // expand argument resolver
        List<HandlerMethodArgumentResolver> argumentResolvers = requestMappingHandlerAdapter.getArgumentResolvers();
        if (argumentResolvers != null) {
            List<HandlerMethodArgumentResolver> ownerArgumentResolvers = Lists.newArrayList(argumentResolvers);
            for (int i = 0; i < ownerArgumentResolvers.size(); i++) {
                HandlerMethodArgumentResolver argumentResolver = ownerArgumentResolvers.get(i);
                // 扩展 RequestResponseBodyMethodProcessor 解密算法
                if (argumentResolver instanceof RequestResponseBodyMethodProcessor && encryptProperties.isEnable()) {
                    EncryptInterface encryptInterface = applicationContext.getBean("encryptInterface", EncryptInterface.class);
                    EncryptHandler encryptHandler = new EncryptHandler(argumentResolver, encryptInterface);
                    ownerArgumentResolvers.set(i, encryptHandler);
                }
            }
            requestMappingHandlerAdapter.setArgumentResolvers(ownerArgumentResolvers);
            log.info("BaseWebMvcConfig expand argument resolver end");
        }

        // expand return value handler
        List<HandlerMethodReturnValueHandler> returnValueHandlers = requestMappingHandlerAdapter.getReturnValueHandlers();
        List<HandlerMethodReturnValueHandler> list = Lists.newArrayList();
        HandlerMethodReturnValueHandler autoResultReturnHandler = new AutoResultReturnHandler();
        // 扩展 autoResultReturnHandler 加密算法
        if (encryptProperties.isEnable()) {
            EncryptInterface encryptInterface = applicationContext.getBean("encryptInterface", EncryptInterface.class);
            autoResultReturnHandler = new EncryptHandler(autoResultReturnHandler, encryptInterface);
        }
        list.add(autoResultReturnHandler);
        if (returnValueHandlers != null) {
            list.addAll(returnValueHandlers);
        }
        requestMappingHandlerAdapter.setReturnValueHandlers(list);
        log.info("BaseWebMvcConfig expand return value handler end");
    }

}
