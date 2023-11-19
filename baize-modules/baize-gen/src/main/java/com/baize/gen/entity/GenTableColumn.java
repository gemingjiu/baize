package com.baize.gen.entity;

import com.baize.common.core.domain.BaseEntity;
import com.baize.common.core.utils.text.StringUtils;

import javax.validation.constraints.NotBlank;

/**
 * @author gemj
 * @since 2023/12/08 00:37
 */
public class GenTableColumn extends BaseEntity {
    private static final long serialVersionUID = 1L;
    /** 归属表编号 */
    private String tableId;

    /** 列名称 */
    private String columnName;

    /** 列描述 */
    private String columnComment;

    /** 列类型 */
    private String columnType;

    /** JAVA类型 */
    private String javaType;

    /** JAVA字段名 */
    @NotBlank(message = "Java属性不能为空")
    private String javaField;

    /** 是否主键（1是） */
    private String pk;

    /** 是否自增（1是） */
    private String increment;

    /** 是否必填（1是） */
    private String required;

    /** 是否为插入字段（1是） */
    private String insert;

    /** 是否编辑字段（1是） */
    private String edit;

    /** 是否列表字段（1是） */
    private String list;

    /** 是否查询字段（1是） */
    private String query;

    /** 查询方式（EQ等于、NE不等于、GT大于、LT小于、LIKE模糊、BETWEEN范围） */
    private String queryType;

    /** 显示类型（input文本框、textarea文本域、select下拉框、checkbox复选框、radio单选框、datetime日期控件、image图片上传控件、upload文件上传控件、editor富文本控件） */
    private String visibleType;

    /** 字典类型 */
    private String dictType;

    public String getTableId() {
        return tableId;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
    }

    public void setColumnName(String columnName)
    {
        this.columnName = columnName;
    }

    public String getColumnName()
    {
        return columnName;
    }

    public void setColumnComment(String columnComment)
    {
        this.columnComment = columnComment;
    }

    public String getColumnComment()
    {
        return columnComment;
    }

    public void setColumnType(String columnType)
    {
        this.columnType = columnType;
    }

    public String getColumnType()
    {
        return columnType;
    }

    public void setJavaType(String javaType)
    {
        this.javaType = javaType;
    }

    public String getJavaType()
    {
        return javaType;
    }

    public void setJavaField(String javaField)
    {
        this.javaField = javaField;
    }

    public String getJavaField()
    {
        return javaField;
    }

    public String getCapJavaField()
    {
        return StringUtils.capitalize(javaField);
    }

    public void setPk(String pk)
    {
        this.pk = pk;
    }

    public String getPk()
    {
        return pk;
    }

    public boolean pk()
    {
        return pk(this.pk);
    }

    public boolean pk(String pk)
    {
        return pk != null && StringUtils.equals("1", pk);
    }

    public String getIncrement()
    {
        return increment;
    }

    public void setIncrement(String increment)
    {
        this.increment = increment;
    }

    public boolean increment()
    {
        return increment(this.increment);
    }

    public boolean increment(String increment)
    {
        return increment != null && StringUtils.equals("1", increment);
    }

    public void setRequired(String required)
    {
        this.required = required;
    }

    public String getRequired()
    {
        return required;
    }

    public boolean required()
    {
        return required(this.required);
    }

    public boolean required(String required)
    {
        return required != null && StringUtils.equals("1", required);
    }

    public void setInsert(String insert)
    {
        this.insert = insert;
    }

    public String getInsert()
    {
        return insert;
    }

    public boolean insert()
    {
        return insert(this.insert);
    }

    public boolean insert(String insert)
    {
        return insert != null && StringUtils.equals("1", insert);
    }

    public void setEdit(String edit)
    {
        this.edit = edit;
    }

    public String getEdit()
    {
        return edit;
    }

    public boolean edit()
    {
        return insert(this.edit);
    }

    public boolean edit(String edit)
    {
        return edit != null && StringUtils.equals("1", edit);
    }

    public void setList(String list)
    {
        this.list = list;
    }

    public String getList()
    {
        return list;
    }

    public boolean list()
    {
        return list(this.list);
    }

    public boolean list(String list)
    {
        return list != null && StringUtils.equals("1", list);
    }

    public void setQuery(String query)
    {
        this.query = query;
    }

    public String getQuery()
    {
        return query;
    }

    public boolean query()
    {
        return query(this.query);
    }

    public boolean query(String query)
    {
        return query != null && StringUtils.equals("1", query);
    }

    public void setQueryType(String queryType)
    {
        this.queryType = queryType;
    }

    public String getQueryType()
    {
        return queryType;
    }

    public String getVisibleType()
    {
        return visibleType;
    }

    public void setVisibleType(String visibleType)
    {
        this.visibleType = visibleType;
    }

    public void setDictType(String dictType)
    {
        this.dictType = dictType;
    }

    public String getDictType()
    {
        return dictType;
    }


    public boolean isSuperColumn()
    {
        return isSuperColumn(this.javaField);
    }

    public static boolean isSuperColumn(String javaField)
    {
        return StringUtils.equalsAnyIgnoreCase(javaField,
                // BaseEntity
                "createdBy", "createdTime", "modifiedBy", "modifiedTime", "remark",
                // TreeEntity
                "parentName", "parentId", "orderNum", "ancestors");
    }

    public boolean isUsableColumn()
    {
        return isUsableColumn(javaField);
    }

    public static boolean isUsableColumn(String javaField)
    {
        // isSuperColumn()中的名单用于避免生成多余Domain属性，若某些属性在生成页面时需要用到不能忽略，则放在此处白名单
        return StringUtils.equalsAnyIgnoreCase(javaField, "parentId", "orderNum", "remark");
    }

    public String readConverterExp()
    {
        String remarks = StringUtils.substringBetween(this.columnComment, "（", "）");
        StringBuffer sb = new StringBuffer();
        if (StringUtils.isNotEmpty(remarks))
        {
            for (String value : remarks.split(" "))
            {
                if (StringUtils.isNotEmpty(value))
                {
                    Object startStr = value.subSequence(0, 1);
                    String endStr = value.substring(1);
                    sb.append("").append(startStr).append("=").append(endStr).append(",");
                }
            }
            return sb.deleteCharAt(sb.length() - 1).toString();
        }
        else
        {
            return this.columnComment;
        }
    }
}
