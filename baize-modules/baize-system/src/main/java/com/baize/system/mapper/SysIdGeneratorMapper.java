package com.baize.system.mapper;

import java.util.List;

import com.baize.system.domain.SysIdGenerator;

/**
 * 自增ID生成Mapper接口
 *
 * @author baize
 * @date 2024-05-13
 */
public interface SysIdGeneratorMapper {
    /**
     * 查询自增ID生成
     *
     * @param id 自增ID生成主键
     * @return 自增ID生成
     */
    public SysIdGenerator selectSysIdGeneratorById(String id);

    /**
     * 查询自增ID生成列表
     *
     * @param sysIdGenerator 自增ID生成
     * @return 自增ID生成集合
     */
    public List<SysIdGenerator> selectSysIdGeneratorList(SysIdGenerator sysIdGenerator);

    /**
     * 新增自增ID生成
     *
     * @param sysIdGenerator 自增ID生成
     * @return 结果
     */
    public int insertSysIdGenerator(SysIdGenerator sysIdGenerator);

    /**
     * 修改自增ID生成
     *
     * @param sysIdGenerator 自增ID生成
     * @return 结果
     */
    public int updateSysIdGenerator(SysIdGenerator sysIdGenerator);

    /**
     * 删除自增ID生成
     *
     * @param id 自增ID生成主键
     * @return 结果
     */
    public int deleteSysIdGeneratorById(String id);

    /**
     * 批量删除自增ID生成
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysIdGeneratorByIds(String[] ids);
}