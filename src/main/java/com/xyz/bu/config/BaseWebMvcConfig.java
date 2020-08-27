package com.xyz.bu.config;

import com.xyz.bu.handler.returnn.AutoResultReturnHandler;
import org.assertj.core.util.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import javax.annotation.Resource;
import java.util.List;

/**
 * 实现InitializingBean的bean在启动时都会被执行afterPropertiesSet
 * 源码 {@link org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#invokeInitMethods}
 */
@Configuration
public class BaseWebMvcConfig implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseWebMvcConfig.class);

    @Resource
    RequestMappingHandlerAdapter requestMappingHandlerAdapter;

    @Override
    public void afterPropertiesSet() {
        List<HandlerMethodReturnValueHandler> returnValueHandlers = requestMappingHandlerAdapter
                .getReturnValueHandlers();
        List<HandlerMethodReturnValueHandler> list = Lists.newArrayList();
        // first add owner handler
        // 封装结果处理
        list.add(new AutoResultReturnHandler());
        if (returnValueHandlers != null) {
            list.addAll(returnValueHandlers);
        }
        requestMappingHandlerAdapter.setReturnValueHandlers(list);
        LOGGER.info("BaseWebMvcConfig add owner return value handlers");
    }

}
