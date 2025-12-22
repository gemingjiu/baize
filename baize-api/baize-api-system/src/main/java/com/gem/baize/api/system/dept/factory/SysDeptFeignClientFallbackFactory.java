package com.gem.baize.api.system.dept.factory;

import com.gem.baize.api.system.dept.client.SysDeptFeignClient;
import com.gem.baize.api.system.dept.domain.dto.SysDeptDTO;
import com.gem.baize.common.core.model.vo.PageResult;
import com.gem.baize.common.core.model.vo.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;

public class SysDeptFeignClientFallbackFactory implements FallbackFactory<SysDeptFeignClient> {
    private static final Logger log = LoggerFactory.getLogger(SysDeptFeignClientFallbackFactory.class);

    @Override
    public SysDeptFeignClient create(Throwable cause) {
        return new SysDeptFeignClient() {

            @Override
            public Result<SysDeptDTO> getById(String id) {
                return null;
            }

            @Override
            public Result<Integer> create(SysDeptDTO sysDeptDTO) {
                return null;
            }

            @Override
            public Result<Void> update(String id, SysDeptDTO dto) {
                return null;
            }

            @Override
            public Result<Void> delete(String id) {
                return null;
            }

            @Override
            public Result<PageResult<SysDeptDTO>> page(int current, int size, SysDeptDTO dto) {
                return null;
            }
        };
    }
}
