package me.codegen;

/**
 * 表字段类
 *
 * @author Administrator
 */
public class ColumnData {

    private String columnName;
    private String dataType;
    private String columnComment;
    private String propertyName;
    private boolean autoIncrement;

    public boolean isAutoIncrement() {
        return this.autoIncrement;
    }

    public void setAutoIncrement(boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getPropertyName() {
        return this.propertyName;
    }

    public String getColumnName() {
        return this.columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getDataType() {
        return this.dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getColumnComment() {
        return this.columnComment;
    }

    public void setColumnComment(String columnComment) {
        this.columnComment = columnComment;
    }
}
