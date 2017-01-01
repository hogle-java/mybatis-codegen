/**
 * Copyright(c) 2011-2015 by YouCredit Inc.
 * All Rights Reserved
 */
package me.codegen

import groovy.swing.SwingBuilder

import java.awt.BorderLayout
import java.awt.EventQueue
import java.awt.Toolkit
import java.text.SimpleDateFormat

import javax.swing.BoxLayout
import javax.swing.JFrame
import javax.swing.JOptionPane
import javax.swing.UIManager

import org.apache.commons.lang.StringUtils
import org.apache.velocity.VelocityContext


/**
 * @author Jianchang Guo
 *
 */
public class Main {
    private static ResourceBundle res = ResourceBundle
            .getBundle("DataSourceConfig");
    static void main(args) {

        EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        try {
                            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                            def frame = new Main()
                            frame.show()
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    void show() {
        def swingBuilder = new SwingBuilder()


        def address = {
            swingBuilder.panel(constraints:BorderLayout.NORTH){
                boxLayout(axis:BoxLayout.X_AXIS)
                label(text:"数据库地址：")
                addressField = textField(columns:15)
                addressField.text = res.getString("gpt.url")
            }
        }

        def name = {
            swingBuilder.panel(constraints:BorderLayout.NORTH){
                boxLayout(axis:BoxLayout.X_AXIS)
                label(text:"用户名：")
                userNameField = textField(columns:15)
                userNameField.text = res.getString("gpt.username")
            }
        }

        def pwd = {
            swingBuilder.panel(constraints:BorderLayout.NORTH){
                boxLayout(axis:BoxLayout.X_AXIS)
                label(text:"密码：")
                pwdField = passwordField(columns:15)
                pwdField.text = res.getString("gpt.password")
            }
        }

        def excludeTables = {
            swingBuilder.panel(constraints:BorderLayout.NORTH){
                boxLayout(axis:BoxLayout.X_AXIS)
                label(text:"排除表(英文逗号分隔)：")
                excludeTablesField = textArea(columns:15, rows:3, lineWrap:true)
                excludeTablesField.text="admin_group,admin_group_role,admin_resource,admin_role,admin_role_resource,admin_user,admin_user_group,admin_user_resource,admin_user_role,app_version,oauth_access_token,oauth_client_details,oauth_client_token,oauth_code,oauth_refresh_token"
            }
        }

        def includeTables = {
            swingBuilder.panel(constraints:BorderLayout.NORTH){
                boxLayout(axis:BoxLayout.X_AXIS)
                label(text:"包含表(英文逗号分隔)：")
                includeTablesField = textField(columns:15)
                includeTablesField.text=""
            }
        }
        def path = {
            swingBuilder.panel(constraints:BorderLayout.NORTH){
                boxLayout(axis:BoxLayout.X_AXIS)
                label(text:"生成代码路径：")
                pathField = textField(columns:15)
                pathField.text = "/Users/wenyu.zhao/java_workspaces"
            }
        }

        def pkg = {
            swingBuilder.panel(constraints:BorderLayout.NORTH){
                boxLayout(axis:BoxLayout.X_AXIS)
                label(text:"包名：")
                pkgField = textField(columns:15)
                pkgField.text = "com.mllh.pay.ma"
            }
        }
        def removePrefix = {
            swingBuilder.panel(constraints:BorderLayout.NORTH){
                boxLayout(axis:BoxLayout.X_AXIS)
                label(text:"去掉前缀：")
                removePrefixCheckBox = checkBox()
            }
        }

        def mainPanel = {
            swingBuilder.panel(constraints:BorderLayout.NORTH){
                boxLayout(axis:BoxLayout.Y_AXIS)
                address()
                name()
                pwd()
                excludeTables()
                includeTables()
                path()
                pkg()
                removePrefix()
                button(text:"生成代码", actionPerformed:{
                    println "${addressField.text} \n ${userNameField.text}\n ${pwdField.text} \n ${excludeTablesField.text} \n ${pathField.text} \n ${pkgField.text} \n ${includeTablesField.text} \n ${removePrefixCheckBox.isSelected()}"
                    String result = perform(addressField.text, userNameField.text, pwdField.text , excludeTablesField.text , pathField.text , pkgField.text, includeTablesField.text, removePrefixCheckBox.isSelected())
                    JOptionPane.showMessageDialog(null, result)
                })
            }
        }

        def toolkit = Toolkit.getDefaultToolkit()
        def screenSize = toolkit.getScreenSize()
        def width = 800
        def height = 600
        int X = (screenSize.width - width) / 2
        int Y = (screenSize.height - height) / 2
        swingBuilder.frame(title:"CodeGen",
        defaultCloseOperation:JFrame.EXIT_ON_CLOSE,
        size:[width, height],
        show:true,
        location: [X, Y]) { mainPanel() }
    }

    String perform(String url, String username, String password, String execudeTables, String path, String pkg, String includeTables, boolean removePrefix) {

        String srcPath = path;
        try {
            CreateBean c = new CreateBean();
            c.setMysqlInfo(url, username, password);
            List<String> tables = null;
            if (StringUtils.isNotBlank(includeTables)) {
                tables = c.getIncludeTables(includeTables.split(","));
            } else {
                tables = c.getTables(execudeTables.split(","));
            }

            for (String table : tables) {
                CreateBean createBean = new CreateBean();
                createBean.setMysqlInfo(url, username,
                        password);
                /** 此处修改成你的 表名 和 中文注释 ***/
                String tableName = table; //表名
                String codeName = table;//中文注释  当然你用英文也是可以的
                String className = createBean.getTablesNameToClassName(tableName, removePrefix);
                String lowerName = StringUtils.uncapitalize(className)

                //项目跟路径路径，此处修改为你的项目路径
                String rootPath = StringUtils.isBlank(path) ? "/Users/wenyu.zhao/java_workspaces" : path;

                //根路径
                srcPath = rootPath + "/src/";
                //包路径
                String pkgPath = pkg.replace(".", "/");
                String pckPath = srcPath + pkgPath + (pkgPath.endsWith("/") || pkgPath.endsWith("/") ? "" : "/");

                //java,xml文件名称
                String modelPath = "model/" + className + ".java";
                String mapperPath = "dao/" + className + "Mapper.java";
                String servicePath = "service/" + className + "Service.java";
                String serviceImplPath = "service/impl/" + className + "ServiceImpl.java";
                String controllerPath = "api/web/" + className + "Controller.java";
                String sqlMapperPath = "conf/mybatis/" + className + "Mapper.xml";

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日aHH:mm:ss");
                VelocityContext context = new VelocityContext();
                context.put("className", className); //
                context.put("lowerName", lowerName);
                context.put("codeName", codeName);
                context.put("tableName", tableName);
                context.put("mapperPkg", pkg + ".dao");
                context.put("modelPkg", pkg + ".model");
                context.put("servicePkg", pkg + ".service");
                context.put("controllerPkg", pkg + ".api.web");
                context.put("author", "codegen");
                context.put("date", sdf.format(new Date()));

                /****************************** 生成bean字段 *********************************/
                context.put("feilds", createBean.getBeanFeilds(tableName)); //生成bean

                /******************************* 生成sql语句 **********************************/
                Map<String, Object> sqlMap = createBean
                        .getAutoCreateSql(tableName);
                context
                        .put("columnDatas", createBean.getColumnDatas(tableName)); //生成bean
                context.put("SQL", sqlMap);

                //-------------------生成文件代码---------------------/
                CommonPageParser.WriterPage(context, "TempModel.java", pckPath,
                        modelPath); //生成Model
                CommonPageParser.WriterPage(context, "TempMapper.java", pckPath,
                        mapperPath); //生成MybatisMapper接口 相当于Dao
                CommonPageParser.WriterPage(context, "TempService.java", pckPath,
                        servicePath);//生成Service
                CommonPageParser.WriterPage(context, "TempServiceImpl.java",
                        pckPath, serviceImplPath);//生成ServiceImpl
                CommonPageParser.WriterPage(context, "TempMapper.xml", pckPath,
                        sqlMapperPath);//生成Mybatis xml配置文件
                CommonPageParser.WriterPage(context, "TempController.java",
                        pckPath, controllerPath);//生成Controller

            }
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        }

        return "生成代码成功！路径为:" + srcPath;
    }
}
