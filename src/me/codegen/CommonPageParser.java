package me.codegen;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;

public class CommonPageParser {

    private static VelocityEngine ve;// = VelocityEngineUtil.getVelocityEngine();

    private static Properties properties;

    private final static String CONTENT_ENCODING = "UTF-8";

    private static final Log log = LogFactory.getLog(CommonPageParser.class);

    private static boolean isReplace = true;  //是否可以替换文件 true =可以替换，false =不可以替换

    /**
     * 获取项目的路径
     *
     * @return
     */
    public static String getRootPath() {
        String rootPath = "";
        try {
            File file = new File(CommonPageParser.class.getResource("/")
                .getFile());
            //eclipse打开
            rootPath = file.getParentFile().getAbsolutePath();
//            //idea打开
//            rootPath = file.getParentFile().getParentFile().getAbsolutePath();
//            rootPath = rootPath+"/codegen";
            rootPath = java.net.URLDecoder.decode(rootPath, "utf-8");
            return rootPath;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rootPath;
    }

    static {
        try {
            //获取文件模板根路径
            String templateBasePath = CommonPageParser.getRootPath()
                    + "/template";
            Properties properties = new Properties();
            properties.setProperty(RuntimeConstants.RESOURCE_LOADER, "file");
            properties.setProperty("file.resource.loader.description",
                    "Velocity File Resource Loader");
            properties.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH,
                templateBasePath);
            properties.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_CACHE,
                    "true");
            properties.setProperty(
                "file.resource.loader.modificationCheckInterval", "30");
            properties.setProperty(
                RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS,
                    "org.apache.velocity.runtime.log.Log4JLogChute");
            properties.setProperty("runtime.log.logsystem.log4j.logger",
                    "org.apache.velocity");
            properties.setProperty("directive.set.null.allowed", "true");
            VelocityEngine velocityEngine = new VelocityEngine();
            velocityEngine.init(properties);
            CommonPageParser.ve = velocityEngine;
        } catch (Exception e) {
            CommonPageParser.log.error(e);
        }
    }

    public static void WriterPage(VelocityContext context, String templateName,
            String fileDirPath, String targetFile) {
        try {
            File file = new File(fileDirPath + targetFile);
            if (!file.exists()) {
                new File(file.getParent()).mkdirs();
            } else {
                if (CommonPageParser.isReplace) {
                    System.out.println("替换文件" + file.getAbsolutePath());
                } else {
                    System.out.println("页面生成失败" + file.getAbsolutePath()
                        + "文件已存在");
                    return;
                }
            }

            Template template = CommonPageParser.ve.getTemplate(templateName,
                CommonPageParser.CONTENT_ENCODING);
            FileOutputStream fos = new FileOutputStream(file);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                fos, CommonPageParser.CONTENT_ENCODING));
            template.merge(context, writer);
            writer.flush();
            writer.close();
            fos.close();
            System.out.println("代码生成成功" + file.getAbsolutePath());
        } catch (Exception e) {
            CommonPageParser.log.error(e);
        }
    }

}
