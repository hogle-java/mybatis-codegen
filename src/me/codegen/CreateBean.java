package me.codegen;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.velocity.util.StringUtils;

public class CreateBean {
    private Connection connection = null;
    static String url;
    static String username;
    static String password;
    static String rt = "\r\t";
    String SQLTables = "show tables";
    static {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setMysqlInfo(String url, String username, String password) {
        CreateBean.url = url;
        CreateBean.username = username;
        CreateBean.password = password;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(CreateBean.url, CreateBean.username,
            CreateBean.password);
    }

    public List<String> getTables() throws SQLException {
        Connection con = this.getConnection();
        List<String> list = new ArrayList<String>();
        ResultSet rs = con.getMetaData().getTables(null, "%", "%",
            new String[] { "TABLE" });

        while (rs.next()) {
            String table = rs.getString("TABLE_NAME");
            list.add(table);
        }
        rs.close();
        con.close();
        return list;
    }

    public List<String> getIncludeTables(String... includes)
            throws SQLException {
        Connection con = this.getConnection();
        List<String> list = new ArrayList<String>();
        ResultSet rs = con.getMetaData().getTables(null, "%", "%",
            new String[] { "TABLE" });

        while (rs.next()) {
            String table = rs.getString("TABLE_NAME");
            for (String e : includes) {
                if (table.equalsIgnoreCase(e)) {
                    list.add(table);
                    break;
                }
            }

        }
        rs.close();
        con.close();
        return list;
    }

    public List<String> getTables(String... excludes) throws SQLException {
        Connection con = this.getConnection();
        List<String> list = new ArrayList<String>();
        ResultSet rs = con.getMetaData().getTables(null, "%", "%",
            new String[] { "TABLE" });

        while (rs.next()) {
            String table = rs.getString("TABLE_NAME");
            boolean exclude = false;
            for (String e : excludes) {
                if (table.equalsIgnoreCase(e)) {
                    exclude = true;
                    break;
                }
            }

            if (!exclude) {
                list.add(table);
            }
        }
        rs.close();
        con.close();
        return list;
    }

    /**
     * 查询表的字段，封装成List
     *
     * @param tableName
     * @returnSELECT distinct COLUMN_NAME, DATA_TYPE, COLUMN_COMMENT FROM
     *               information_schema.columns WHERE table_name =
     *               '"+tableName+"' SELECT distinct COLUMN_NAME, DATA_TYPE,
     *               COLUMN_COMMENT FROM information_schema.columns WHERE
     *               table_name = '"+tableName+"' SELECT distinct COLUMN_NAME,
     *               DATA_TYPE, COLUMN_COMMENT FROM information_schema.columns
     *               WHERE table_name = '"+tableName+"' SELECT distinct
     *               COLUMN_NAME, DATA_TYPE, COLUMN_COMMENT FROM
     *               information_schema.columns WHERE table_name =
     *               '"+tableName+"' SELECT distinct COLUMN_NAME, DATA_TYPE,
     *               COLUMN_COMMENT FROM information_schema.columns WHERE
     *               table_name = '"+tableName+"' SELECT distinct COLUMN_NAME,
     *               DATA_TYPE, COLUMN_COMMENT FROM information_schema.columns
     *               WHERE table_name = '"+tableName+"' SELECT distinct
     *               COLUMN_NAME, DATA_TYPE, COLUMN_COMMENT FROM
     *               information_schema.columns WHERE table_name =
     *               '"+tableName+"'
     *               com.mysql.jdbc.Drivercom.mysql.jdbc.Driverjdbc
     *               :mysql://127.0.0.1:3306/ems
     * @throws SQLException
     */
    public List<ColumnData> getColumnDatas(String tableName)
            throws SQLException {
        String SQLColumns = "SELECT distinct COLUMN_NAME, DATA_TYPE, COLUMN_COMMENT FROM information_schema.columns WHERE table_name =  '"
            + tableName + "' ";

        Connection con = this.getConnection();
        ResultSet columnRS = con.createStatement().executeQuery(SQLColumns);
        Map<String, String> remarks = new HashMap<>();
        while (columnRS.next()) {
            remarks.put(columnRS.getString("COLUMN_NAME"),
                columnRS.getString("COLUMN_COMMENT"));
        }
        columnRS.close();
//        ResultSet r = con.getMetaData().getTables(null, "%", "%", new String[]{"TABLE"});
//        r.next();
//        r.getString("TABLE_NAME");
        SQLColumns = "select * from " + tableName;
        PreparedStatement ps = con.prepareStatement(SQLColumns);
        List<ColumnData> columnList = new ArrayList<ColumnData>();
        ResultSet rs = ps.executeQuery();
        StringBuffer str = new StringBuffer();
        StringBuffer getset = new StringBuffer();
        ResultSetMetaData rsm = rs.getMetaData();
        for (int i = 1; i <= rsm.getColumnCount(); i++) {
            System.out.print("Column[JavaType: " + rsm.getColumnClassName(i));
            System.out.print(" Name: " + rsm.getColumnName(i));
            System.out.println(" DBType : " + rsm.getColumnTypeName(i) + "]");
            String columnName = rsm.getColumnName(i);
            String type = rsm.getColumnClassName(i);
            String comment = org.apache.commons.lang.StringUtils
                .trimToEmpty(remarks.get(columnName));
            String propertyName = "";
            boolean autoIncr = rsm.isAutoIncrement(i);
            type = this.getType(type);

            if (columnName.contains("_")) {
                String arrs[] = columnName.split("_");
                String temp = "";
                int k = 0;
                for (String s : arrs) {
                    temp = temp
                        + (k++ == 0 ? s.toLowerCase() : StringUtils
                            .capitalizeFirstLetter(s.toLowerCase()));
                }
                propertyName = temp;
            } else {
                propertyName = columnName.toLowerCase();
            }
            ColumnData cd = new ColumnData();
            cd.setColumnName(columnName);
            cd.setDataType(type);
            cd.setColumnComment(comment);
            cd.setPropertyName(propertyName);
            cd.setAutoIncrement(autoIncr);
            columnList.add(cd);

        }
//        while(rs.next()){
//        	String name = rs.getString(1);
//			String type = rs.getString(2);
//			String comment = rs.getString(3);
//			type=this.getType(type);
//
//			if (name.contains("_")) {
//				String arrs[]  = name.split("_");
//				String temp = "";
//				int i = 0;
//				for (String s : arrs) {
//					temp = temp + (i++ == 0 ? s.toLowerCase() : StringUtils.capitalizeFirstLetter(s.toLowerCase()));
//				}
//				name = temp;
//			} else {
//				name = name.toLowerCase();
//			}
//			ColumnData cd= new ColumnData();
//			cd.setColumnName(name);
//			cd.setDataType(type);
//			cd.setColumnComment(comment);
//			columnList.add(cd);
//        }
        this.argv = str.toString();
        this.method = getset.toString();
        rs.close();
        ps.close();
        con.close();
        return columnList;
    }

    private String method;
    private String argv;

    /**
     * 生成实体Bean 的属性和set,get方法
     *
     * @param tableName
     * @return
     * @throws SQLException
     */
    public String getBeanFeilds(String tableName) throws SQLException {
        List<ColumnData> dataList = this.getColumnDatas(tableName);
        StringBuffer str = new StringBuffer();
        StringBuffer getset = new StringBuffer();
        for (ColumnData d : dataList) {
            String name = d.getPropertyName();
            String type = d.getDataType();
            String comment = d.getColumnComment();
            //type=this.getType(type);
            String maxChar = name.substring(0, 1).toUpperCase();
            str.append("\r\t").append("/**\n").append("\t * ").append(comment)
                .append("\n").append("\t */\n\t").append("private ")
                .append(type + " ").append(name).append(";\n");
            String method = maxChar + name.substring(1, name.length());
            getset.append("\r\t").append("public ").append(type + " ")
                .append("get" + method + "() {\r\t");
            getset.append("    return this.").append(name).append(";\r\t}");
            getset.append("\r\t").append("public void ")
                .append("set" + method + "(" + type + " " + name + ") {\r\t");
            getset.append("    this." + name + "=").append(name)
                .append(";\r\t}");
        }
        this.argv = str.toString();
        this.method = getset.toString();
        return this.argv + this.method;
    }

    public String getType(String type) {

        if ("char".equalsIgnoreCase(type) || "varchar".equalsIgnoreCase(type)) {
            return "String";
        } else if ("int".equalsIgnoreCase(type)) {
            return "Integer";
        } else if ("bigint".equalsIgnoreCase(type)) {
            return "Long";
        } else if ("timestamp".equalsIgnoreCase(type)
            || "date".equalsIgnoreCase(type)
            || "datetime".equalsIgnoreCase(type)) {
            return "java.sql.Timestamp";
        }
        if ("java.sql.Timestamp".equals(type)) {
            type = "java.util.Date";
        }
        return type;
    }

    public void getPackage(int type, String createPath, String content,
            String packageName, String className, String extendsClassName,
            String... importName) throws Exception {
        if (null == packageName) {
            packageName = "";
        }
        StringBuffer sb = new StringBuffer();
        sb.append("package ").append(packageName).append(";\r");
        sb.append("\r");
        for (String element : importName) {
            sb.append("import ").append(element).append(";\r");
        }
        sb.append("\r");
        sb.append("/**\r *  entity. @author wolf Date:"
            + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
            + "\r */");
        sb.append("\r");
        sb.append("\rpublic class ").append(className);
        if (null != extendsClassName) {
            sb.append(" extends ").append(extendsClassName);
        }
        if (type == 1) { //bean
            sb.append(" ").append("implements java.io.Serializable {\r");
        } else {
            sb.append(" {\r");
        }
        sb.append("\r\t");
        sb.append("private static final long serialVersionUID = 1L;\r\t");
        String temp = className.substring(0, 1).toLowerCase();
        temp += className.substring(1, className.length());
        if (type == 1) {
            sb.append("private " + className + " " + temp + "; // entity ");
        }
        sb.append(content);
        sb.append("\r}");
        System.out.println(sb.toString());
        this.createFile(createPath, "", sb.toString());
    }

    public String getTablesNameToClassName(String tableName,
            boolean removePrefix) {
        String[] split = tableName.split("_");
        if (split.length > 1) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < split.length; i++) {
                String element = split[i];
                if (i == 0 && removePrefix) {
                    continue;
                }
                String tempTableName = element.substring(0, 1).toUpperCase()
                    + element.substring(1, element.length());
                sb.append(tempTableName);
            }
            System.out.println(sb.toString());
            return sb.toString();
        } else {
            String tempTables = split[0].substring(0, 1).toUpperCase()
                + split[0].substring(1, split[0].length());
            return tempTables;
        }
    }

    public void createFile(String path, String fileName, String str)
            throws IOException {
        FileWriter writer = new FileWriter(new File(path + fileName));
        writer.write(new String(str.getBytes("utf-8")));
        writer.flush();
        writer.close();
    }

    static String selectStr = "select ";
    static String from = " from ";

    public Map<String, Object> getAutoCreateSql(String tableName)
            throws Exception {
        Map<String, Object> sqlMap = new HashMap<String, Object>();
        List<ColumnData> columnDatas = this.getColumnDatas(tableName);
        String columns = this.getColumnSplit(columnDatas);
        String properties = this.getColumnPropertySplit(columnDatas);
        String[] columnList = this.getColumnList(columns);  //表所有字段
        String[] propertyList = this.getColumnList(properties);
        String columnFields = this.getColumnFields(columns); //表所有字段 按","隔开

        String insert = "insert into "
            + tableName
            + "("
            + this.getInsertColumnSplit(columnDatas).replaceAll("\\|", ",")
            + ")\n values(#{"
            + this.getInsertColumnPropertySplit(columnDatas).replaceAll("\\|",
                "},#{") + "})";
        String update = this.getUpdateSql(tableName, columnList, propertyList);
        String updateSelective = this.getUpdateSelectiveSql(tableName,
            columnDatas);
        String selectById = this.getSelectByIdSql(tableName, columnList,
            propertyList);
        String delete = this.getDeleteSql(tableName, columnList, propertyList);
        sqlMap.put("columnList", columnList);
        sqlMap.put("columnFields", columnFields);
        sqlMap.put("insert", insert);
        sqlMap.put("update", update);
        sqlMap.put("delete", delete);
        sqlMap.put("updateSelective", updateSelective);
        sqlMap.put("selectById", selectById);
        return sqlMap;
    }

    /**
     * delete
     *
     * @param tableName
     * @param columnsList
     * @return
     * @throws SQLException
     */
    public String getDeleteSql(String tableName, String[] columnsList,
            String[] propertyList) throws SQLException {
        StringBuffer sb = new StringBuffer();
        sb.append("delete ");
        sb.append("\t from ").append(tableName).append(" where ");
        sb.append(columnsList[0]).append(" = #{").append(propertyList[0])
            .append("}");
        return sb.toString();
    }

    /**
     * 根据id查询
     *
     * @param tableName
     * @param columnsList
     * @return
     * @throws SQLException
     */
    public String getSelectByIdSql(String tableName, String[] columnsList,
            String[] propertyList) throws SQLException {
        StringBuffer sb = new StringBuffer();
        sb.append("select <include refid=\"columnList\" /> \n");
        sb.append("\t from ").append(tableName).append(" where ");
        sb.append(columnsList[0]).append(" = #{").append(propertyList[0])
            .append("}");
        return sb.toString();
    }

    /**
     * 获取所有的字段，并按","分割
     *
     * @param columns
     * @return
     * @throws SQLException
     */
    public String getColumnFields(String columns) throws SQLException {
        String fields = columns;
        if (fields != null && !"".equals(fields)) {
            fields = fields.replaceAll("[|]", ",");
        }
        return fields;
    }

    /**
     * @param columns
     * @return
     * @throws SQLException
     */
    public String[] getColumnList(String columns) throws SQLException {
        String[] columnList = columns.split("[|]");
        return columnList;
    }

    /**
     * 修改记录
     *
     * @param tableName
     * @param columnsList
     * @return
     * @throws SQLException
     */
    public String getUpdateSql(String tableName, String[] columnsList,
            String[] propertyList) throws SQLException {
        StringBuffer sb = new StringBuffer();

        for (int i = 1; i < columnsList.length; i++) {
            String column = columnsList[i];
            String property = propertyList[i];
            sb.append(column + "=#{" + property + "}");
            //最后一个字段不需要","
            if (i + 1 < columnsList.length) {
                sb.append(",");
            }
        }
        String update = "update " + tableName + " set " + sb.toString()
            + " where " + columnsList[0] + "=#{" + propertyList[0] + "}";
        return update;
    }

    public String getUpdateSelectiveSql(String tableName,
            List<ColumnData> columnList) throws SQLException {
        StringBuffer sb = new StringBuffer();
        ColumnData cd = columnList.get(0); //获取第一条记录，主键
        sb.append("\t<trim  suffixOverrides=\",\" >\n");
        for (int i = 1; i < columnList.size(); i++) {
            ColumnData data = columnList.get(i);
            String columnName = data.getColumnName();
            sb.append("\t<if test=\"").append(columnName).append(" != null ");
            //String 还要判断是否为空
            if ("String" == data.getDataType()) {
                sb.append(" and ").append(columnName).append(" != ''");
            }
            sb.append(" \">\n\t\t");
            sb.append(columnName + "=#{" + columnName + "},\n");
            sb.append("\t</if>\n");
        }
        sb.append("\t</trim>");
        String update = "update " + tableName + " set \n" + sb.toString()
            + " where " + cd.getColumnName() + "=#{" + cd.getColumnName() + "}";
        return update;
    }

    public String getColumnSplit(List<ColumnData> columnList)
            throws SQLException {
        StringBuffer commonColumns = new StringBuffer();
        for (ColumnData data : columnList) {
            commonColumns.append(data.getColumnName() + "|");
        }
        return commonColumns.delete(commonColumns.length() - 1,
            commonColumns.length()).toString();
    }

    public String getInsertColumnSplit(List<ColumnData> columnList)
            throws SQLException {
        StringBuffer commonColumns = new StringBuffer();
        for (ColumnData data : columnList) {
            if (!data.isAutoIncrement()) {
                commonColumns.append(data.getColumnName() + "|");
            }
        }
        return commonColumns.delete(commonColumns.length() - 1,
            commonColumns.length()).toString();
    }

    public String getColumnPropertySplit(List<ColumnData> columnList)
            throws SQLException {
        StringBuffer commonColumns = new StringBuffer();
        for (ColumnData data : columnList) {
            commonColumns.append(data.getPropertyName() + "|");
        }
        return commonColumns.delete(commonColumns.length() - 1,
            commonColumns.length()).toString();
    }

    public String getInsertColumnPropertySplit(List<ColumnData> columnList)
            throws SQLException {
        StringBuffer commonColumns = new StringBuffer();
        for (ColumnData data : columnList) {
            if (!data.isAutoIncrement()) {
                commonColumns.append(data.getPropertyName() + "|");
            }
        }
        return commonColumns.delete(commonColumns.length() - 1,
            commonColumns.length()).toString();
    }

}
