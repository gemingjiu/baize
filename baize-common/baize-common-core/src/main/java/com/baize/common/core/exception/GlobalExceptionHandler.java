package com.baize.common.core.exception;

import com.baize.common.core.constant.HttpStatus;
import com.baize.common.core.domain.Response;
import com.baize.common.core.enums.BaizeExceptionEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * @author gemj
 * @since 2024/02/26 20:38
 */
@RestControllerAdvice
@Order(value = 0)
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * DuplicateKeyException
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public Response<Object> handleDuplicateKeyExceptionException(DuplicateKeyException e) {
        log.error("违反唯一性约束,重复主键'{}'", e.getMessage());
        Response<Object> res = new Response<>();
        res.setCode(BaizeExceptionEnum.DB_DUPLICATE_KEY_EXCEPTION.getCode());
        res.setMsg(BaizeExceptionEnum.DB_DUPLICATE_KEY_EXCEPTION.getMsg());
        return res;
    }

    @ExceptionHandler({BaseException.class})
    public Response<Object> handleBaseException(BaseException e, HttpServletRequest request) {
        log.error("请求地址'{}',内部错误码:{}.", request.getRequestURI(), e);
        Response<Object> res = new Response<>();
        res.setCode(HttpStatus.ERROR);
        res.setData(e);
        res.setMsg(e.getMessage());
        return res;
    }
}
