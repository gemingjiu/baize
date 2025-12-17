package com.gem.baize.api.admin.dept.factory;

import com.gem.baize.api.admin.dept.client.DeptFeignClient;
import com.gem.baize.api.admin.dept.domain.dto.DeptDTO;
import com.gem.baize.common.core.model.vo.PageResult;
import com.gem.baize.common.core.model.vo.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;

public class DeptFeignClientFallbackFactory implements FallbackFactory<DeptFeignClient> {
    private static final Logger log = LoggerFactory.getLogger(DeptFeignClientFallbackFactory.class);

    @Override
    public DeptFeignClient create(Throwable cause) {
        return new DeptFeignClient() {

            @Override
            public Result<DeptDTO> getById(String id) {
                return null;
            }

            @Override
            public Result<Integer> create(DeptDTO deptDTO) {
                return null;
            }

            @Override
            public Result<Void> update(String id, DeptDTO dto) {
                return null;
            }

            @Override
            public Result<Void> delete(String id) {
                return null;
            }

            @Override
            public Result<PageResult<DeptDTO>> page(int current, int size, DeptDTO dto) {
                return null;
            }
        };
    }
}
