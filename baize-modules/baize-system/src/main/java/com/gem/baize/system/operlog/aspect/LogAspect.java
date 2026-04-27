package com.gem.baize.system.operlog.aspect;

import com.alibaba.fastjson2.JSON;
import com.gem.baize.api.system.operlog.domain.dto.SysOperLogDto;
import com.gem.baize.common.core.annotation.Log;
import com.gem.baize.system.operlog.service.SysOperLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

/**
 * 操作日志记录处理
 */
@Slf4j
@Aspect
@Component
public class LogAspect {

    @Autowired
    private SysOperLogService sysOperLogService;

    /**
     * 处理完请求后执行
     */
    @AfterReturning(pointcut = "@annotation(controllerLog)", returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, Log controllerLog, Object jsonResult) {
        handleLog(joinPoint, controllerLog, null, jsonResult);
    }

    /**
     * 拦截异常操作
     */
    @AfterThrowing(pointcut = "@annotation(controllerLog)", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Log controllerLog, Exception e) {
        handleLog(joinPoint, controllerLog, e, null);
    }

    @Async
    protected void handleLog(final JoinPoint joinPoint, final Log controllerLog, final Exception e, Object jsonResult) {
        try {
            // 获取请求信息
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                return;
            }
            HttpServletRequest request = attributes.getRequest();

            // 创建日志对象
            SysOperLogDto operLog = new SysOperLogDto();
            operLog.setStatus(0);

            // 设置IP地址
            String ip = getIpAddr(request);
            operLog.setOperIp(ip);

            // 设置请求URL
            operLog.setOperUrl(request.getRequestURI());

            // 设置操作用户信息
            String userId = request.getHeader("X-User-Id");
            String tenantId = request.getHeader("X-Tenant-Id");
            String username = request.getHeader("X-Username");
            if (username != null) {
                operLog.setUserName(username);
            }
            if (tenantId != null) {
                operLog.setTenantId(tenantId);
            }

            if (e != null) {
                operLog.setStatus(1);
                operLog.setErrorMsg(e.getMessage());
            }

            // 设置方法名称
            String className = joinPoint.getTarget().getClass().getName();
            String methodName = joinPoint.getSignature().getName();
            operLog.setMethod(className + "." + methodName + "()");

            // 设置请求方式
            operLog.setRequestMethod(request.getMethod());

            // 设置注解上的信息
            operLog.setTitle(controllerLog.title());
            operLog.setBusinessType(controllerLog.businessType().ordinal());
            operLog.setOperatorType(controllerLog.operatorType().ordinal());

            // 是否需要保存请求参数
            if (controllerLog.isSaveRequestData()) {
                setRequestValue(joinPoint, operLog, controllerLog.excludeParamNames());
            }

            // 是否需要保存响应参数
            if (controllerLog.isSaveResponseData() && jsonResult != null) {
                String result = JSON.toJSONString(jsonResult);
                if (result.length() > 5000) {
                    result = result.substring(0, 5000) + "...";
                }
                operLog.setJsonResult(result);
            }

            operLog.setOperTime(LocalDateTime.now());
            operLog.setCostTime(0L);

            // 保存日志
            sysOperLogService.insertOperlog(operLog);

        } catch (Exception exp) {
            log.error("操作日志记录异常: {}", exp.getMessage(), exp);
        }
    }

    /**
     * 获取请求参数
     */
    private void setRequestValue(JoinPoint joinPoint, SysOperLogDto operLog, String[] excludeParamNames) {
        try {
            Object[] args = joinPoint.getArgs();
            if (args == null || args.length == 0) {
                return;
            }

            // 过滤掉不需要记录的参数
            Object[] filteredArgs = Arrays.stream(args)
                    .filter(arg -> {
                        if (arg == null) return false;
                        String className = arg.getClass().getName();
                        // 排除特殊类型
                        return !className.contains("HttpServletRequest")
                                && !className.contains("HttpServletResponse")
                                && !className.contains("MultipartFile");
                    })
                    .toArray();

            if (filteredArgs.length == 0) {
                return;
            }

            String params = JSON.toJSONString(filteredArgs.length == 1 ? filteredArgs[0] : filteredArgs);
            if (params.length() > 2000) {
                params = params.substring(0, 2000) + "...";
            }
            operLog.setOperParam(params);
        } catch (Exception e) {
            log.warn("获取请求参数失败: {}", e.getMessage());
        }
    }

    /**
     * 获取IP地址
     */
    private String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("X-Real-IP");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多个代理时取第一个IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
