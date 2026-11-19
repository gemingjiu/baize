package com.gem.baize.common.webmvc.exception;

import com.gem.baize.common.core.enums.ErrorCode;
import com.gem.baize.common.core.exception.model.*;
import com.gem.baize.common.core.model.vo.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import jakarta.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理数据库唯一键冲突异常
     */
    @ExceptionHandler(DuplicateKeyException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiResult<Void> handleDuplicateKeyException(DuplicateKeyException e, HttpServletRequest request) {
        log.warn("数据重复: {} - {}", request.getRequestURI(), e.getMessage());
        return ApiResult.fail(ErrorCode.DATA_ALREADY_EXISTS.code(), "数据已存在，请更换后重试");
    }

    /**
     * 处理数据库访问异常
     */
    @ExceptionHandler(DataAccessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResult<Void> handleDataAccessException(DataAccessException e, HttpServletRequest request) {
        log.error("数据库访问异常: {} - {}", request.getRequestURI(), e.getMessage(), e);
        return ApiResult.fail(ErrorCode.SYSTEM_ERROR.code(), "数据库操作失败，请稍后重试");
    }

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BaseException.class)
    public ApiResult<Void> handleBaseException(BaseException e, HttpServletRequest request) {
        log.warn("业务异常: {} - {}", request.getRequestURI(), e.getMessage());
        return ApiResult.fail(e.getErrorCode().code(), e.getMessage());
    }

    /**
     * 处理参数异常
     */
    @ExceptionHandler(ParamException.class)
    public ApiResult<Void> handleParamException(ParamException e, HttpServletRequest request) {
        log.warn("参数异常: {} - {}", request.getRequestURI(), e.getMessage());
        return ApiResult.fail(ErrorCode.INVALID_PARAM.code(), e.getMessage());
    }

    /**
     * 处理数据不存在异常
     */
    @ExceptionHandler(NotFoundException.class)
    public ApiResult<Void> handleNotFoundException(NotFoundException e, HttpServletRequest request) {
        log.warn("数据不存在: {} - {}", request.getRequestURI(), e.getMessage());
        return ApiResult.fail(ErrorCode.DATA_NOT_EXISTS.code(), e.getMessage());
    }

    /**
     * 处理重复数据异常
     */
    @ExceptionHandler(DuplicateException.class)
    public ApiResult<Void> handleDuplicateException(DuplicateException e, HttpServletRequest request) {
        log.warn("数据重复: {} - {}", request.getRequestURI(), e.getMessage());
        return ApiResult.fail(ErrorCode.DATA_ALREADY_EXISTS.code(), e.getMessage());
    }

    /**
     * 处理参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResult<Void> handleValidationException(MethodArgumentNotValidException e, HttpServletRequest request) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn("参数校验失败: {} - {}", request.getRequestURI(), message);
        return ApiResult.fail(ErrorCode.INVALID_PARAM.code(), message);
    }

    /**
     * 处理绑定异常
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResult<Void> handleBindException(BindException e, HttpServletRequest request) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn("参数绑定失败: {} - {}", request.getRequestURI(), message);
        return ApiResult.fail(ErrorCode.INVALID_PARAM.code(), message);
    }

    /**
     * 处理非法参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResult<Void> handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {
        log.warn("非法参数: {} - {}", request.getRequestURI(), e.getMessage());
        return ApiResult.fail(ErrorCode.INVALID_PARAM.code(), e.getMessage());
    }

    /**
     * 处理未授权异常
     */
    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResult<Void> handleUnauthorizedException(UnauthorizedException e, HttpServletRequest request) {
        log.warn("未授权访问: {} - {}", request.getRequestURI(), e.getMessage());
        return ApiResult.fail(ErrorCode.UNAUTHORIZED.code(), e.getMessage());
    }

    /**
     * 处理禁止访问异常
     */
    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiResult<Void> handleForbiddenException(ForbiddenException e, HttpServletRequest request) {
        log.warn("禁止访问: {} - {}", request.getRequestURI(), e.getMessage());
        return ApiResult.fail(ErrorCode.FORBIDDEN.code(), e.getMessage());
    }

    /**
     * 处理未映射路径（含未暴露的 actuator 端点）。
     * <p>
     * 必须排在 {@link #handleException} 之前单独处理：否则"客户端请求了不存在的路径"会落进
     * 兜底分支返回 500「系统内部错误」，让监控把客户端错误误判为服务故障并触发告警，
     * 也会污染 5xx 错误率指标。Boot 3 抛的是 {@link NoResourceFoundException}，
     * Boot 2 时代的 {@link NoHandlerFoundException} 一并兜住。
     */
    @ExceptionHandler({NoResourceFoundException.class, NoHandlerFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResult<Void> handleNotFoundPath(Exception e, HttpServletRequest request) {
        log.warn("路径不存在: {} {}", request.getMethod(), request.getRequestURI());
        return ApiResult.fail(ErrorCode.RESOURCE_NOT_FOUND.code(), ErrorCode.RESOURCE_NOT_FOUND.message());
    }

    /**
     * 处理其他未知异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResult<Void> handleException(Exception e, HttpServletRequest request) {
        log.error("系统异常: {} - {}", request.getRequestURI(), e.getMessage(), e);
        return ApiResult.fail(ErrorCode.SYSTEM_ERROR.code(), "系统内部错误，请稍后重试");
    }
}
