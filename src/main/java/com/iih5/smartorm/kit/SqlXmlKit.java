package com.iih5.smartorm.kit;

import com.alibaba.fastjson.JSON;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            Resource[] dr = resolver.getResources("classpath:" + sqlDir + "/*");
            if (dr == null || dr.length == 0) {
                logger.warn("找不到sql文件目录！");
                return;
            }
            for (Resource r : dr) {
                loadXmlResource(r);
            }
        } catch (Exception e) {
            logger.error("读取sql xml 文件异常", e);
        }
    }

    public SqlXmlKit(String... paths) {
        try {
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            for (String path : paths) {
                Resource[] dr = resolver.getResources("classpath:" + path + "/*");
                for (Resource r : dr) {
                    loadXmlResource(r);
                }
            }
        } catch (Exception e) {
            logger.error("加载sql文件异常", e);
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

    //1 清洗括号()
    private static String cleanBracket(String sql) {
        StringBuffer sbf = new StringBuffer();
        String[] list = sql.split("\\s+");
        for (int i = 0; i < list.length; i++) {
            String a = list[i];
            if (a.contains("${")) {
                sbf.append(a.replace("(", "( ").replace(")", " )"));
            } else {
                sbf.append(a);
            }
            sbf.append(" ");
        }
        return sbf.toString();
    }

    //2 替换${}的参数
    private static String replaceParam(String str, Map<String, Object> map) {
        String patternString = "\\$\\{(" + StringUtils.join(map.keySet(), "|") + ")\\}";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            Object d = map.get(matcher.group(1));
            if (d == null) {
                matcher.appendReplacement(sb, "'NULL'");
            } else if (d instanceof Long || d instanceof Double || d instanceof Integer || d instanceof Float || d instanceof BigInteger) {
                matcher.appendReplacement(sb, String.valueOf(d));
            } else {
                if (d instanceof Collection || d instanceof List || d instanceof  Set) {
                    String st1 = JSON.toJSONString(d);
                    String dd = st1.substring(st1.indexOf("[") + 1, st1.indexOf("]"));
                    matcher.appendReplacement(sb, dd);
                } else {
                    matcher.appendReplacement(sb, "'" + d + "'");
                }
            }
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    //清除NULL和空括号
    private static String cleanNULL(String str) {
        StringBuffer sb = new StringBuffer();
        boolean ignore = false;
        String[] list = str.split("\\s+");
        for (int i = 0; i < list.length; i++) {
            String a = list[i];
            if (ignore != true && !a.contains("'NULL'")&& !a.contains("${")) {
                sb.append(a);
                sb.append("  ");
            } else {
                ignore = false;
            }
            if (a.contains("'NULL'") && (i + 1 < list.length) && (list[i + 1].toLowerCase().equals("and".toLowerCase())
                    || list[i + 1].toLowerCase().equals("or".toLowerCase()))) {
                ignore = true;
            }
        }
        String tmpResult = sb.toString().replaceAll("\\(  \\)", "");
        StringBuffer sb2 = new StringBuffer();
        String[] list2 = tmpResult.split("\\s+");
        for (int i = 0; i < list2.length; i++) {
            if (list2[i].toLowerCase().equals("and".toLowerCase()) || list2[i].toLowerCase().equals("or".toLowerCase())) {
                if (i + 1 >= list2.length) {
                    continue;
                }
                if ((!list2[i + 1].contains("=") && !list2[i + 1].contains(">") && !list2[i + 1].contains("<") && !list2[i + 1].contains("("))
                        || (!list2[i - 1].contains("=") && !list2[i - 1].contains(">") && !list2[i - 1].contains("<") && !list2[i - 1].contains(")"))) {
                    if ((i+2<list2.length) && (!list2[i+2].toLowerCase().equals("in") && !list2[i+2].toLowerCase().equals("in(") && !list2[i+2].toLowerCase().equals("not"))){
                        continue;
                    }
                }
            }
            String a = list2[i];
            sb2.append(a);
            sb2.append(" ");
        }
        return sb2.toString();
    }

    /**
     * 智能组装sql语句 注：sql里面的函数如果没有输入值的话必须为test() ,不能为test( )即里面不能有空格键，= > < 左右不能留空格
     *
     * @param sql
     * @param object 可以是 map 也可以是bean 的形式
     * @return
     */
    public static String autoAssembleSQL(String sql, Object object) {
        String a = cleanBracket(sql);
        String c = "";
        if (object instanceof Map) {
            String b = replaceParam(a, (Map) object);
            c = cleanNULL(b);
        } else {
            Map map = beanToMap(object);
            String b = replaceParam(a, map);
            c = cleanNULL(b);
        }
        return c;
    }

    private static Map beanToMap(Object bean) {
        try {
            Map<String, Object> map = new HashMap<String, Object>();
            Field[] fields = bean.getClass().getDeclaredFields();
            for (Field field : fields) {
                PropertyDescriptor pd = new PropertyDescriptor(field.getName(), bean.getClass());
                Method methodReader = pd.getReadMethod();
                Object value = methodReader.invoke(bean);
                if (value != null) {
                    map.put(field.getName(), value);
                }
            }
            return map;
        } catch (Exception e) {
            logger.error("转换异常", e);
        }
        return null;
    }

}



