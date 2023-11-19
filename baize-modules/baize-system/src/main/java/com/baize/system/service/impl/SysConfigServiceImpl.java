package com.baize.system.service.impl;


import com.baize.common.cache.service.CacheService;
import com.baize.common.core.constant.CacheConstants;
import com.baize.common.core.constant.UserConstants;
import com.baize.common.core.exception.BaseException;
import com.baize.common.core.utils.ConvertUtils;
import com.baize.common.core.utils.text.StringUtils;
import com.baize.common.core.utils.uuid.UUID;
import com.baize.common.security.utils.SecurityUtils;
import com.baize.system.domain.SysConfig;
import com.baize.system.mapper.SysConfigMapper;
import com.baize.system.service.ISysConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.List;

import static com.baize.common.core.enums.BaizeExceptionEnum.SERVICE_EXCEPTION;

/**
 * 参数配置 服务层实现
 */
@Service
public class SysConfigServiceImpl implements ISysConfigService {
    @Autowired
    private SysConfigMapper configMapper;

    @Autowired
    private CacheService cacheService;
    private void initSysConfig(SysConfig info) {
        info.setId(UUID.fastUUID().toString(true));
        info.setCreatedBy(SecurityUtils.getLoginUser().getUsername());
        info.setModifiedBy(SecurityUtils.getLoginUser().getUsername());
    }
    /**
     * 项目启动时，初始化参数到缓存
     */
    @PostConstruct
    public void init() {
        loadingConfigCache();
    }

    /**
     * 查询参数配置信息
     *
     * @param configId 参数配置ID
     * @return 参数配置信息
     */
    @Override
    public SysConfig selectConfigById(String configId) {
        SysConfig config = new SysConfig();
        config.setId(configId);
        return configMapper.selectConfig(config);
    }

    /**
     * 根据键名查询参数配置信息
     *
     * @param configKey 参数key
     * @return 参数键值
     */
    @Override
    public String selectConfigByKey(String configKey) {
        String configValue = ConvertUtils.toStr(cacheService.getCacheObject(getCacheKey(configKey)));
        if (StringUtils.isNotEmpty(configValue)) {
            return configValue;
        }
        SysConfig config = new SysConfig();
        config.setConfigKey(configKey);
        SysConfig retConfig = configMapper.selectConfig(config);
        if (StringUtils.isNotNull(retConfig)) {
            cacheService.setCacheObject(getCacheKey(configKey), retConfig.getConfigValue());
            return retConfig.getConfigValue();
        }
        return StringUtils.EMPTY;
    }

    /**
     * 查询参数配置列表
     *
     * @param config 参数配置信息
     * @return 参数配置集合
     */
    @Override
    public List<SysConfig> selectConfigList(SysConfig config) {
        return configMapper.selectConfigList(config);
    }

    /**
     * 新增参数配置
     *
     * @param config 参数配置信息
     * @return 结果
     */
    @Override
    public int insertConfig(SysConfig config) {
        initSysConfig(config);
        int row = configMapper.insertConfig(config);
        if (row > 0) {
            cacheService.setCacheObject(getCacheKey(config.getConfigKey()), config.getConfigValue());
        }
        return row;
    }

    /**
     * 修改参数配置
     *
     * @param config 参数配置信息
     * @return 结果
     */
    @Override
    public int updateConfig(SysConfig config) {
        SysConfig temp = configMapper.selectConfigById(config.getId());
        if (!StringUtils.equals(temp.getConfigKey(), config.getConfigKey())) {
            cacheService.deleteObject(getCacheKey(temp.getConfigKey()));
        }

        int row = configMapper.updateConfig(config);
        if (row > 0) {
            cacheService.setCacheObject(getCacheKey(config.getConfigKey()), config.getConfigValue());
        }
        return row;
    }

    /**
     * 批量删除参数信息
     *
     * @param configIds 需要删除的参数ID
     */
    @Override
    public void deleteConfigByIds(String[] configIds) {
        for (String configId : configIds) {
            SysConfig config = selectConfigById(configId);
            if (StringUtils.equals(UserConstants.YES, config.getConfigType())) {
                throw new BaseException(SERVICE_EXCEPTION, String.format("内置参数【%1$s】不能删除 ", config.getConfigKey()));
            }
            configMapper.deleteConfigById(configId);
            cacheService.deleteObject(getCacheKey(config.getConfigKey()));
        }
    }

    /**
     * 加载参数缓存数据
     */
    @Override
    public void loadingConfigCache() {
        List<SysConfig> configsList = configMapper.selectConfigList(new SysConfig());
        for (SysConfig config : configsList) {
            cacheService.setCacheObject(getCacheKey(config.getConfigKey()), config.getConfigValue());
        }
    }

    /**
     * 清空参数缓存数据
     */
    @Override
    public void clearConfigCache() {
        Collection<String> keys = cacheService.keys(CacheConstants.SYS_CONFIG_KEY + "*");
        cacheService.deleteObject(keys);
    }

    /**
     * 重置参数缓存数据
     */
    @Override
    public void resetConfigCache() {
        clearConfigCache();
        loadingConfigCache();
    }

    /**
     * 校验参数键名是否唯一
     *
     * @param config 参数配置信息
     * @return 结果
     */
    @Override
    public boolean checkConfigKeyUnique(SysConfig config) {
        String configId = StringUtils.isNull(config.getId()) ? "" : config.getId();
        SysConfig info = configMapper.checkConfigKeyUnique(config.getConfigKey());
        if (StringUtils.isNotNull(info) && StringUtils.equals(info.getId(), configId)) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 设置cache key
     *
     * @param configKey 参数键
     * @return 缓存键key
     */
    private String getCacheKey(String configKey) {
        return CacheConstants.SYS_CONFIG_KEY + configKey;
    }
}
