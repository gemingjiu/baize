package com.gem.baize.common.core.exception.handle;

import com.gem.baize.common.core.exception.model.BadRequestException;
import com.gem.baize.common.core.exception.model.BaseException;
import com.gem.baize.common.core.exception.model.ForbiddenException;
import com.gem.baize.common.core.exception.model.NotFoundException;
import com.gem.baize.common.core.model.vo.Result;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常拦截器
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 数据库统一拦截异常
    @ExceptionHandler(DuplicateKeyException.class)
    public Result<Void> handleDuplicateKey(DuplicateKeyException e) {
        return Result.error(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(DataAccessException.class)
    public Result<Void> handleDataAccessException(DataAccessException e) {
        return Result.error(HttpStatus.CONFLICT, e.getMessage());
    }

    // 处理参数校验异常
    @ExceptionHandler(BadRequestException.class)
    public Result<Void> handleBadRequest(BadRequestException e) {
        return Result.error(e);
    }

    // 处理资源不存在异常
    @ExceptionHandler(NotFoundException.class)
    public Result<Void> handleNotFound(NotFoundException e) {
        return Result.error(e);
    }

    // 处理权限不足异常
    @ExceptionHandler(ForbiddenException.class)
    public Result<Void> handleForbidden(ForbiddenException e) {
        return Result.error(e);
    }

    // 处理 BaseException
    @ExceptionHandler(BaseException.class)
    public Result<Void> handleBusinessException(BaseException e) {
        return Result.error(e); // 自动转换
    }

    // 处理兜底异常
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        return Result.error(HttpStatus.INTERNAL_SERVER_ERROR, "服务器错误: " + e.getMessage());
    }
}
