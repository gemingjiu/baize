package com.gem.baize.api.system.dept.factory;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gem.baize.api.system.dept.client.SysDeptFeignClient;
import com.gem.baize.api.system.dept.domain.dto.SysDeptDto;
import com.gem.baize.common.core.enums.ErrorCode;
import com.gem.baize.common.core.model.vo.ApiResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;

import java.util.Collections;
import java.util.List;

public class SysDeptFeignClientFallbackFactory implements FallbackFactory<SysDeptFeignClient> {
    private static final Logger log = LoggerFactory.getLogger(SysDeptFeignClientFallbackFactory.class);

    @Override
    public SysDeptFeignClient create(Throwable cause) {
        log.error("[Feign] baize-system 部门服务调用失败，进入降级", cause);
        return new SysDeptFeignClient() {

            @Override
            public ApiResult<Integer> create(SysDeptDto sysDeptDTO) {
                return ApiResult.fail(ErrorCode.DEPENDENCY_ERROR.code(), ErrorCode.DEPENDENCY_ERROR.message());
            }

            @Override
            public ApiResult<Page<SysDeptDto>> page(int current, int size, SysDeptDto dto) {
                return ApiResult.fail(ErrorCode.DEPENDENCY_ERROR.code(), ErrorCode.DEPENDENCY_ERROR.message());
            }

            @Override
            public ApiResult<SysDeptDto> getById(String id) {
                return ApiResult.fail(ErrorCode.DEPENDENCY_ERROR.code(), ErrorCode.DEPENDENCY_ERROR.message());
            }

            @Override
            public ApiResult<Void> update(String id, SysDeptDto dto) {
                return ApiResult.fail(ErrorCode.DEPENDENCY_ERROR.code(), ErrorCode.DEPENDENCY_ERROR.message());
            }

            @Override
            public ApiResult<Void> delete(String id) {
                return ApiResult.fail(ErrorCode.DEPENDENCY_ERROR.code(), ErrorCode.DEPENDENCY_ERROR.message());
            }

            @Override
            public ApiResult<List<SysDeptDto>> tree(SysDeptDto dto) {
                return ApiResult.fail(ErrorCode.DEPENDENCY_ERROR.code(), ErrorCode.DEPENDENCY_ERROR.message());
            }

            @Override
            public ApiResult<List<SysDeptDto>> treeselect(String tenantId) {
                return ApiResult.fail(ErrorCode.DEPENDENCY_ERROR.code(), ErrorCode.DEPENDENCY_ERROR.message());
            }

            @Override
            public ApiResult<List<SysDeptDto>> list(SysDeptDto dto) {
                return ApiResult.fail(ErrorCode.DEPENDENCY_ERROR.code(), ErrorCode.DEPENDENCY_ERROR.message());
            }
        };
    }
}
