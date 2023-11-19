package com.baize.gateway.handler;

import com.baize.common.core.domain.AjaxResult;
import com.baize.common.core.exception.BaseException;
import com.baize.gateway.service.CaptchaCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.io.IOException;

/**
 * 验证码获取
 *
 * @author gemj
 */
@Component
public class CaptchaHandler implements HandlerFunction<ServerResponse> {

    @Autowired
    private CaptchaCodeService captchaCodeService;

    @Override
    public Mono<ServerResponse> handle(ServerRequest request) {
        AjaxResult ajax;
        try {
            ajax = captchaCodeService.createCaptcha();
        } catch (BaseException | IOException e) {
            return Mono.error(e);
        }
        return ServerResponse.status(HttpStatus.OK).body(BodyInserters.fromValue(ajax));
    }
}
