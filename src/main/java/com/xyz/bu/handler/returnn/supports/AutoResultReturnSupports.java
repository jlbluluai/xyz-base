package com.xyz.bu.handler.returnn.supports;

import com.alibaba.fastjson.JSON;
import com.xyz.bu.common.ActionStatus;
import com.xyz.bu.common.BaseConstant;
import com.xyz.bu.exception.BusinessException;
import com.xyz.bu.handler.returnn.JsonResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author xyz
 * @date 2020/7/17
 */
public abstract class AutoResultReturnSupports {

    private static final Logger LOGGER = LoggerFactory.getLogger(AutoResultReturnSupports.class);

    /**
     * different type to supportsReturnType
     *
     * @param methodParameter
     * @param param
     * @return
     */
    public abstract boolean supportsReturnType(@NonNull MethodParameter methodParameter, @Nullable String param);

    /**
     * default handleReturnValue, if need commission please override
     *
     * @param o
     * @param methodParameter
     * @param modelAndViewContainer
     * @param nativeWebRequest
     */
    public void handleReturnValue(Object o, MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest) {
        modelAndViewContainer.setRequestHandled(true);
        HttpServletResponse response = nativeWebRequest.getNativeResponse(HttpServletResponse.class);
        response.setContentType("application/json;charset=UTF-8");
        try (PrintWriter writer = response.getWriter()) {
            JsonResult jsonResult = new JsonResult();
            jsonResult.setData(o);
            jsonResult.setStatus(ActionStatus.OK.getValue());
            jsonResult.setDesc(ActionStatus.OK.getReason());
            // 取出开始时间 计算耗费
            Long startTime = BaseConstant.START_TIME.get();
            if (startTime != null) {
                jsonResult.setCost(System.currentTimeMillis() - startTime + "ms");
            }
            writer.print(JSON.toJSONString(jsonResult));
            writer.flush();
        } catch (IOException e) {
            LOGGER.error("#AutoResultReturnHandler io handler ex ", e);
            throw new BusinessException("服务器开小差，请稍后再试");
        } finally {
            BaseConstant.START_TIME.remove();
        }
    }
}
