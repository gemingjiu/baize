package com.baize.common.core.advice;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.baize.common.core.constant.HttpStatus;
import com.baize.common.core.domain.Response;
import com.baize.common.core.enums.BaizeException;
import com.baize.common.core.exception.ApiException;
import com.baize.common.core.exception.SystemException;

/**
 * @author gemj
 * @since 2024/02/26 20:38
 */
@RestControllerAdvice
@Order(value = 0)
public class GlobalExceptionAdvice {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionAdvice.class);

    /**
     * DuplicateKeyException
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public Response<Object> handleDuplicateKeyExceptionException(DuplicateKeyException e) {
        logger.error("违反唯一性约束,重复主键'{}'", e.getMessage());
        Response<Object> res = new Response<>();
        res.setCode(BaizeException.DB_DUPLICATE_KEY_EXCEPTION.getCode());
        res.setMsg(BaizeException.DB_DUPLICATE_KEY_EXCEPTION.getMessage());
        return res;
    }

    @ExceptionHandler({ApiException.class})
    public Response<Object> handleApiException(ApiException e, HttpServletRequest request) {
        return doHandleException(e, request);
    }

    @ExceptionHandler({SystemException.class})
    public Response<Object> handleSystemException(ApiException e, HttpServletRequest request) {
        return doHandleException(e, request);
    }

    private Response<Object> doHandleException(ApiException e, HttpServletRequest request) {
        String formatMsg = String.format("module:[%s] msg:[%s]", e.getModule(), e.getMessage());
        logger.error("请求地址'{}',错误信息:{}.", request.getRequestURI(), formatMsg);
        Response<Object> res = new Response<>();
        res.setCode(HttpStatus.ERROR);
        res.setData(e.getData());
        res.setMsg(formatMsg);
        return res;
    }
}
