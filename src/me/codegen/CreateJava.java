package me.codegen;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.velocity.VelocityContext;

public class CreateJava {
    private static ResourceBundle res = ResourceBundle
        .getBundle("DataSourceConfig");
    private static String url = CreateJava.res.getString("gpt.url");
    private static String username = CreateJava.res.getString("gpt.username");
    private static String passWord = CreateJava.res.getString("gpt.password");

    public static void main(String[] args) throws Exception {
        CreateBean c = new CreateBean();
        c.setMysqlInfo(CreateJava.url, CreateJava.username, CreateJava.passWord);
        List<String> tables = c.getTables("sync_job_log");

        for (String table : tables) {
            CreateBean createBean = new CreateBean();
            createBean.setMysqlInfo(CreateJava.url, CreateJava.username,
                CreateJava.passWord);
            /** 此处修改成你的 表名 和 中文注释 ***/
            String tableName = table; //表名
            String codeName = table;//中文注释  当然你用英文也是可以的
            String className = createBean.getTablesNameToClassName(tableName,
                false);
            String lowerName = className.substring(0, 1).toLowerCase()
                + className.substring(1, className.length());

            //项目跟路径路径，此处修改为你的项目路径
            String rootPath = "c:/codegen";

            //根路径
            String srcPath = rootPath + "src\\";
            //包路径
            String pckPath = rootPath + "src\\com\\ucredit\\";
            //页面路径，放到WEB-INF下面是为了不让手动输入路径访问jsp页面，起到安全作用
            String webPath = rootPath + "WebRoot\\WEB-INF\\jsp\\";

            File file = new File(pckPath);
            //java,xml文件名称
            String modelPath = "model\\sales\\" + className + ".java";
            String beanPath = "bean\\" + className + ".java";
            String mapperPath = "dao\\sales\\" + className + "Mapper.java";
            String servicePath = "service\\sales\\" + className
                + "Service.java";
            String serviceImplPath = "service\\sales\\impl\\" + className
                + "ServiceImpl.java";
            String controllerPath = "api\\web\\" + className
                + "Controller.java";
            String sqlMapperPath = "conf\\mybatis\\" + className + "Mapper.xml";
            String springPath = "conf\\spring\\";
            String sqlMapPath = "conf\\mybatis\\";
            //jsp页面路径
            String pageEditPath = lowerName + "\\" + lowerName + "Edit.jsp";
            String pageListPath = lowerName + "\\" + lowerName + "List.jsp";

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日aHH:mm:ss");
            VelocityContext context = new VelocityContext();
            context.put("className", className); //
            context.put("lowerName", lowerName);
            context.put("codeName", codeName);
            context.put("tableName", tableName);
            context.put("mapperPkg", "com.ucredit.dao.sales");
            context.put("modelPkg", "com.ucredit.model.sales");
            context.put("servicePkg", "com.ucredit.service.sales");
            context.put("controllerPkg", "com.ucredit.api.web");
            context.put("author", "bobby");
            context.put("date", sdf.format(new Date()));

            /****************************** 生成bean字段 *********************************/
            try {
                context.put("feilds", createBean.getBeanFeilds(tableName)); //生成bean
            } catch (Exception e) {
                e.printStackTrace();
            }

            /******************************* 生成sql语句 **********************************/
            try {
                Map<String, Object> sqlMap = createBean
                    .getAutoCreateSql(tableName);
                context
                    .put("columnDatas", createBean.getColumnDatas(tableName)); //生成bean
                context.put("SQL", sqlMap);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            //-------------------生成文件代码---------------------/
//			CommonPageParser.WriterPage(context, "TempBean.java",pckPath, beanPath);  //生成实体Bean
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
                pckPath, controllerPath);//生成Controller 相当于接口

        }

    }
}
