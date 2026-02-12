package com.gem.baize.api.system.dept.factory;

import com.gem.baize.api.system.dept.client.SysDeptFeignClient;
import com.gem.baize.api.system.dept.domain.dto.SysDeptDto;
import com.gem.baize.common.core.model.vo.PageResult;
import com.gem.baize.common.core.model.vo.ApiResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;

public class SysDeptFeignClientFallbackFactory implements FallbackFactory<SysDeptFeignClient> {
    private static final Logger log = LoggerFactory.getLogger(SysDeptFeignClientFallbackFactory.class);

    @Override
    public SysDeptFeignClient create(Throwable cause) {
        return new SysDeptFeignClient() {

            @Override
            public ApiResult<SysDeptDto> getById(String id) {
                return null;
            }

            @Override
            public ApiResult<Integer> create(SysDeptDto sysDeptDTO) {
                return null;
            }

            @Override
            public ApiResult<Void> update(String id, SysDeptDto dto) {
                return null;
            }

            @Override
            public ApiResult<Void> delete(String id) {
                return null;
            }

            @Override
            public ApiResult<PageResult<SysDeptDto>> page(int current, int size, SysDeptDto dto) {
                return null;
            }
        };
    }
}
