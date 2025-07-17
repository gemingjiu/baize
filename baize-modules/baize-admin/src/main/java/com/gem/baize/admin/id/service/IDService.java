package com.gem.baize.admin.id.service;

public interface IDService<T, R> {
    /**
     * 生成实体类型的友好ID
     *
     * @param entityType 实体类型 (如 "user", "order")
     * @return 生成的友好ID (如 "usr-1a2b3c4d")
     */
    R generate(String entityType);

    /**
     * 从友好ID解析出原始ID
     *
     * @param publicId 对外暴露的ID
     * @return 原始ID
     */
    T decode(R publicId);

    /**
     * 将原始ID编码为对外暴露的ID
     *
     * @param internalId 原始ID
     * @param entityType 实体类型
     * @return 对外暴露的ID
     */
    R encode(T internalId, String entityType);

    /**
     * 验证ID是否有效
     *
     * @param id 要验证的ID
     * @return 是否有效
     */
    boolean validate(R id);

    /**
     * 从ID中获取实体类型
     *
     * @param id 友好ID
     * @return 实体类型 (如 "user")
     */
    String getType(R id);
}