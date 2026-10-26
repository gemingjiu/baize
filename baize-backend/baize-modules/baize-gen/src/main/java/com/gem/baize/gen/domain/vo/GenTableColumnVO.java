package com.gem.baize.gen.domain.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 代码生成表字段信息
 */
@Data
public class GenTableColumnVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 列名称 */
    private String columnName;

    /** 列描述 */
    private String columnComment;

    /** 列类型 */
    private String columnType;

    /** Java属性类型 */
    private String javaType;

    /** Java属性名 */
    private String javaField;

    /** 是否主键 */
    private boolean primaryKey;

    /** 是否自增 */
    private boolean autoIncrement;

    /** 是否必填 */
    private boolean required;

    /** 是否为插入字段 */
    private boolean insert;

    /** 是否为编辑字段 */
    private boolean edit;

    /** 是否为列表字段 */
    private boolean list;

    /** 是否为查询字段 */
    private boolean query;

    /** 查询方式（EQ、NE、GT、LT、LIKE） */
    private String queryType;

    /** 显示类型（input、textarea、select等） */
    private String htmlType;

    /**
     * 是否为 BaseEntity 基类已声明的公共字段（id / tenant_id / sort / version / status /
     * deleted / created_* / modified_* / remark）。
     *
     * <p>派生属性：模板用它决定是否输出该字段。继承 BaseEntity 的 Entity 若重复声明这些属性
     * 会编译失败（重复字段），前端表格/表单也不该出现这些审计列。
     */
    public boolean isBaseColumn() {
        return com.gem.baize.gen.util.GenUtils.isBaseEntityField(this.columnName);
    }
}
