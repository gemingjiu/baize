package com.gem.baize.common.database.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.gem.baize.common.database.handler.BaseEntityObjectHandler;
import com.gem.baize.common.database.handler.CustomDataScopeHandler;
import com.gem.baize.common.database.handler.CustomTenantHandler;
import com.gem.baize.common.database.handler.DataScopeDeptProvider;
import com.gem.baize.common.database.generator.Uuidv7IdentifierGenerator;
import com.gem.baize.common.database.interceptor.DataScopeInnerInterceptor;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Slf4j
@Configuration
@EnableConfigurationProperties(DataScopeProperties.class)
public class MybatisPlusConfig {

    /**
     * 数据权限所需的"部门解析"实现由业务模块（baize-system）提供。
     * 这里用 ObjectProvider 延迟取，避免 common-database 反向依赖业务模块，
     * 也允许未接入数据权限的服务在没有该 Bean 时正常启动。
     */
    private final ObjectProvider<DataScopeDeptProvider> deptProvider;

    public MybatisPlusConfig(ObjectProvider<DataScopeDeptProvider> deptProvider) {
        this.deptProvider = deptProvider;
    }

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(DataScopeProperties dataScopeProperties) {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 多租户插件（行级隔离：tenant_id = X）
        interceptor.addInnerInterceptor(new TenantLineInnerInterceptor(new CustomTenantHandler()));
        // 数据权限插件（可见范围：dept_id IN (...) / create_by = X），默认关闭，见 DataScopeProperties.enabled
        // 注意：这里传 Supplier 而不是 getIfAvailable() 的结果。MybatisPlusInterceptor 是 SqlSessionFactory
        // 的前置依赖，而部门解析实现（baize-system 的 SysDataScopeDeptProvider）自身要注入 Mapper，
        // 构造期取实体会造成 mybatisPlusInterceptor → provider → mapper → sqlSessionFactory → 拦截器 的循环依赖。
        interceptor.addInnerInterceptor(new DataScopeInnerInterceptor(
                dataScopeProperties, new CustomDataScopeHandler(), deptProvider::getIfAvailable));
        // 开启分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.POSTGRE_SQL));
        // 乐观锁插件
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());

        return interceptor;
    }

    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new BaseEntityObjectHandler();
    }

    /**
     * 全局主键生成器：UUIDv7（带连字符 36 位）。
     * MybatisPlusAutoConfiguration#getBeanThen 优先取容器中的该 Bean，
     * 覆盖 MP 自带的 DefaultIdentifierGenerator（雪花 Long）。
     */
    @Bean
    public IdentifierGenerator identifierGenerator() {
        return new Uuidv7IdentifierGenerator();
    }

    @PostConstruct
    public void init() {
        log.info(">>> MybatisPlusHandlerConfig 加载成功！");
    }
}
