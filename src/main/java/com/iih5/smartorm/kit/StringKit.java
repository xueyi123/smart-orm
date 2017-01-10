/**
 * Copyright (c) 2011-2016, James Zhan 詹波 (jfinal@126.com).
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.iih5.smartorm.kit;

import com.iih5.smartorm.generator.GeneratorTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * StringKit.
 */
public class StringKit {
    static Logger logger = LoggerFactory.getLogger(StringKit.class);
    /**
     * 首字母变小写
     */
    public static String firstCharToLowerCase(String str) {
        char firstChar = str.charAt(0);
        if (firstChar >= 'A' && firstChar <= 'Z') {
            char[] arr = str.toCharArray();
            arr[0] += ('a' - 'A');
            return new String(arr);
        }
        return str;
    }

    /**
     * 首字母变大写
     */
    public static String firstCharToUpperCase(String str) {
        char firstChar = str.charAt(0);
        if (firstChar >= 'a' && firstChar <= 'z') {
            char[] arr = str.toCharArray();
            arr[0] -= ('a' - 'A');
            return new String(arr);
        }
        return str;
    }

    /**
     * 字符串为 null 或者为  "" 时返回 true
     * @param str
     * @return
     */
    public static boolean isBlank(String str) {
        return str == null || "".equals(str.trim());
    }

    /**
     * 字符串不为 null 而且不为  "" 时返回 true
     * @param str
     * @return
     */
    public static boolean notBlank(String str) {
        return str != null && !"".equals(str.trim());
    }

    /**
     * 字符串不为 null 而且不为  "" 时返回 true
     * @param strings
     * @return
     */
    public static boolean notBlank(String... strings) {
        if (strings == null)
            return false;
        for (String str : strings)
            if (str == null || "".equals(str.trim()))
                return false;
        return true;
    }

    /**
     * 判断对象是否为空指针
     * @param paras
     * @return
     */
    public static boolean notNull(Object... paras) {
        if (paras == null)
            return false;
        for (Object obj : paras)
            if (obj == null)
                return false;
        return true;
    }

    /**
     * 将划线方式命名的字符串转换为驼峰式
     * @param underscore 下划线命名字符串
     * @return 转换驼峰式命名的字符串
     */
    public static String toCamelCaseName(String underscore) {
        if (underscore.indexOf('_') == -1)
            return underscore;

        underscore = underscore.toLowerCase();
        char[] fromArray = underscore.toCharArray();
        char[] toArray = new char[fromArray.length];
        int j = 0;
        for (int i = 0; i < fromArray.length; i++) {
            if (fromArray[i] == '_') {
                // 当前字符为下划线时，将指针后移一位，将紧随下划线后面一个字符转成大写并存放
                i++;
                if (i < fromArray.length)
                    toArray[j++] = Character.toUpperCase(fromArray[i]);
            } else {
                toArray[j++] = fromArray[i];
            }
        }
        return new String(toArray, 0, j);
    }

    /**
     *将驼峰式命名的字符串转换为下划线方式
     * @param camelCase 转换前的驼峰式命名的字符串
     * @return 转换后下划线方式命名的字符串
     */
    public static String toUnderscoreName(String camelCase) {
        StringBuilder result = new StringBuilder();
        if (camelCase != null && camelCase.length() > 0) {
            result.append(camelCase.substring(0, 1));
            for (int i = 1; i < camelCase.length(); i++) {
                String s = camelCase.substring(i, i + 1);
                if (s.equals(s.toUpperCase()) && !Character.isDigit(s.charAt(0))) {
                    result.append("_");
                }
                result.append(s);
            }
        }
        return result.toString().toLowerCase();
    }

    /**
     * 字符串连接
     * @param stringArray
     * @return
     */
    public static String join(String[] stringArray) {
        StringBuilder sb = new StringBuilder();
        for (String s : stringArray)
            sb.append(s);
        return sb.toString();
    }

    /**
     * 字符串连接
     * @param stringArray
     * @param separator
     * @return
     */
    public static String join(String[] stringArray, String separator) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < stringArray.length; i++) {
            if (i > 0)
                sb.append(separator);
            sb.append(stringArray[i]);
        }
        return sb.toString();
    }


    /**
     * 根据表名返回对应的Model名
     * @param tableName
     * @return
     */
    public static String toModelNameByTable(String tableName) {
        if (GeneratorTool.isHashPrefix) {
//            int index = tableName.indexOf("_");
//            tableName = tableName.substring(index);
            tableName = tableName.substring(tableName.indexOf("_")+1);
            int index = tableName.indexOf("_");
            tableName = tableName.substring(index+1);
        }
        return toCamelCaseName(tableName);
    }

    /**
     * 有bean对象转换为sql查询条件字符串
     * @param cdtBean
     * @return
     */
    public static String beanToSqlConditionStr(Object cdtBean) {
        try {
            StringBuffer condition = new StringBuffer();
            Field[] fields = cdtBean.getClass().getDeclaredFields();
            for (Field field : fields) {
                PropertyDescriptor pd = new PropertyDescriptor(field.getName(), cdtBean.getClass());
                Method methodReader = pd.getReadMethod();
                Object value = methodReader.invoke(cdtBean);
                if (value != null) {
                    if (value instanceof Integer || value instanceof Double || value instanceof BigDecimal ||
                            value instanceof Float || value instanceof Long || value instanceof BigInteger) {
                        condition.append(" and " + StringKit.toUnderscoreName(field.getName()) + "=" + value);
                    } else {
                        condition.append(" and " + StringKit.toUnderscoreName(field.getName()) + "='" + value + "'");
                    }
                }
            }
            return condition.toString();
        } catch (Exception e) {
            logger.error("异常",e);
        }
        return "";
    }

    /**
     * 判断是否为基础类型
     * @param clazz
     * @return
     */
    public static boolean isBaseDataType(Class clazz) {
        try {
            return
                    (
                            clazz.equals(String.class) ||
                            clazz.equals(Integer.class) ||
                            clazz.equals(Byte.class) ||
                            clazz.equals(Long.class) ||
                            clazz.equals(Double.class) ||
                            clazz.equals(Float.class) ||
                            clazz.equals(Character.class) ||
                            clazz.equals(Short.class) ||
                            clazz.equals(BigDecimal.class) ||
                            clazz.equals(BigInteger.class) ||
                            clazz.equals(Boolean.class) ||
                            clazz.equals(java.sql.Date.class) ||
                                    clazz.isPrimitive()
                    );
        } catch (Exception e) {
            logger.error("异常",e);
        }
        return false;
    }
}




