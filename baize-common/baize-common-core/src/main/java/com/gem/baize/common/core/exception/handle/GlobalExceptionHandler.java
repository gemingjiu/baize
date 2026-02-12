package com.gem.baize.common.core.exception.handle;

import com.gem.baize.common.core.exception.model.*;
import com.gem.baize.common.core.model.vo.ApiResult;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常拦截器
 */
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler {

    // 数据库统一拦截异常
    @ExceptionHandler(DuplicateKeyException.class)
    public ApiResult<Void> handleDuplicateKey(DuplicateKeyException e) {
        return ApiResult.fail(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(DuplicateException.class)
    public ApiResult<Void> handleDuplicate(DuplicateException e) {
        return ApiResult.fail(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(DataAccessException.class)
    public ApiResult<Void> handleDataAccessException(DataAccessException e) {
        return ApiResult.fail(HttpStatus.CONFLICT, e.getMessage());
    }

    // 处理参数校验异常
    @ExceptionHandler(ParamException.class)
    public ApiResult<Void> handleBadRequest(ParamException e) {
        return ApiResult.fail(e);
    }

    // 处理资源不存在异常
    @ExceptionHandler(NotFoundException.class)
    public ApiResult<Void> handleNotFound(NotFoundException e) {
        return ApiResult.fail(e);
    }

    // 处理权限不足异常
    @ExceptionHandler(AuthException.class)
    public ApiResult<Void> handleForbidden(AuthException e) {
        return ApiResult.fail(e);
    }

    // 处理 BaseException
    @ExceptionHandler(BaseException.class)
    public ApiResult<Void> handleBusinessException(BaseException e) {
        return ApiResult.fail(e); // 自动转换
    }

    // 处理兜底异常
    @ExceptionHandler(Exception.class)
    public ApiResult<Void> handleException(Exception e) {
        return ApiResult.fail(HttpStatus.INTERNAL_SERVER_ERROR, "服务器错误: " + e.getMessage());
    }
}
