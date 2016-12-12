package com.iih5.smartorm.kit;

import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用于xml配置sql语句的解决插件
 */
public class SqlXmlKit {
   static Logger logger = LoggerFactory.getLogger(SqlXmlKit.class);
    // map<className,<method,sql>>
    private static HashMap<String, Map<String, String>> resourcesMap = new HashMap<String, Map<String, String>>();
    private static String sqlDir = "sql";

    public SqlXmlKit() {
        kk();
    }

    private void kk() {
        try {
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] dr = resolver.getResources("classpath:"+sqlDir+"/*");
            if (dr == null || dr.length==0) {
                logger.warn("找不到sql文件目录！");
                return;
            }
            for (Resource r:dr) {
              loadXmlResource(r);
            }
        } catch (Exception e) {
            logger.error("读取sql xml 文件异常",e);
        }
    }

    public SqlXmlKit(String[] paths) {
        try{
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            for (String path:paths) {
                Resource[] dr = resolver.getResources("classpath:"+path+"/*");
                for (Resource r:dr) {
                    loadXmlResource(r);
                }
            }
        }catch (Exception e){
            logger.error("加载sql文件异常",e);
        }
    }

    private void loadXmlResource(Resource r) throws Exception {
        if (r.getFilename().contains(".xml")) {
            SAXReader reader = new SAXReader();
            Document document = reader.read(r.getInputStream());
            Element xmlRoot = document.getRootElement();
            Map<String, String> methods = new HashMap<String, String>();
            for (Object ebj : xmlRoot.elements("sql")) {
                Element sql = (Element) ebj;
                String text = sql.getText();
                String rs = text.replaceAll("<!\\[CDATA\\[", "").replaceAll("\\]\\]>", "");
                methods.put(sql.attribute("method").getValue(), rs);
            }
            resourcesMap.put(r.getFilename().replace(".xml", ""), methods);
        }
    }


//    private void init(File dataDir) {
//        try {
//            List<File> files = new ArrayList<File>();
//            listDirectory(dataDir, files);
//            for (File file : files) {
//                if (file.getName().contains(".xml")) {
//                    SAXReader reader = new SAXReader();
//                    Document document = reader.read(file);
//                    Element xmlRoot = document.getRootElement();
//                    Map<String, String> methods = new HashMap<String, String>();
//                    for (Object ebj : xmlRoot.elements("sql")) {
//                        Element sql = (Element) ebj;
//                        String text = sql.getText();
//                        String rs = text.replaceAll("<!\\[CDATA\\[", "").replaceAll("\\]\\]>", "");
//                        methods.put(sql.attribute("method").getValue(), rs);
//                    }
//                    resourcesMap.put(file.getName().replace(".xml", ""), methods);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

//    /**
//     * 遍历目录及其子目录下的所有文件并保存
//     *
//     * @param path
//     * @param files
//     */
//    private void listDirectory(File path, List<File> files) {
//        if (path.exists()) {
//            if (path.isFile()) {
//                files.add(path);
//            } else {
//                File[] list = path.listFiles();
//                for (int i = 0; i < list.length; i++) {
//                    listDirectory(list[i], files);
//                }
//            }
//        }
//    }

    /**
     * 获取sql语句
     *
     * @param className 类指针
     * @param method    方法名字
     * @return 返回配置的sql语句
     */
    public static String getSQL(String className, String method) {
        String name = className;
        Map<String, String> m = resourcesMap.get(name);
//        if (debug) {
//            return loadSqlByDebug(name, method);
//        }
        return m.get(method);
    }

    /**
     * 获取sql语句
     *
     * @return
     */
    public static String thisSQL() {
        //获取调用调用此方法的上一级类
        String name = Thread.currentThread().getStackTrace()[2].getFileName().replace(".java", "");
        //获取调用thisSQL方法的上一级方法
        String method = Thread.currentThread().getStackTrace()[2].getMethodName();
        return getSQL(name, method);
    }

//    private static String loadSqlByDebug(String fileName, String method) {
//        String path = sqlDir + "/" + fileName + ".xml";
//        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
//        if (in == null) {
//            logger.warn("找不到文件:" + path);
//            return null;
//        }
//        try {
//            SAXReader reader = new SAXReader();
//            Document document = reader.read(in);
//            Element xmlRoot = document.getRootElement();
//            for (Object ebj : xmlRoot.elements("sql")) {
//                Element sql = (Element) ebj;
//                String methodName = sql.attribute("method").getValue().trim();
//                if (method.equals(methodName)) {
//                    String text = sql.getText();
//                    String rs = text.replaceAll("<!\\[CDATA\\[", "").replaceAll("\\]\\]>", "");
//                    return rs;
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

}



