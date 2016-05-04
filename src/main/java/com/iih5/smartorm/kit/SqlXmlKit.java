/**
 * Copyright (c) 2011-2016, James Zhan 詹波 (jfinal@126.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.iih5.smartorm.kit;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用于xml配置sql语句的解决插件
 */
public class SqlXmlKit {
    // map<className,<method,sql>>
    private static   HashMap<String, Map<String, String>> resourcesMap = new HashMap<String, Map<String, String>>();
    public SqlXmlKit(){
        init("sql");
    }
    public SqlXmlKit(String resources){
        init(resources);
    }
    private void init(String resources)  {
        try {
        URL url=Thread.currentThread().getContextClassLoader().getResource(resources);
        File dataDir = new File(url.toURI());
        List<File> files = new ArrayList<File>();
        listDirectory(dataDir, files);
        for(File file : files){
            if (file.getName().contains(".xml")){
                SAXReader reader = new SAXReader();
                Document document = reader.read(file);
                Element xmlRoot = document.getRootElement();
                for (Object e: xmlRoot.elements("class")) {
                    Map<String,String> methods= new HashMap<String, String>();
                    Element clasz= (Element)e;
                    for (Object ebj:clasz.elements("sql")) {
                        Element sql= (Element)ebj;
                        methods.put(sql.attribute("method").getValue(), sql.getText());
                    }
                    resourcesMap.put(clasz.attributeValue("name"),methods);
                }
           }
        }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 遍历目录及其子目录下的所有文件并保存
     * @param path
     * @param files
     */
    private  void listDirectory(File path, List<File>files){
        if (path.exists()){
            if (path.isFile()){
                files.add(path);
            } else{
                File[] list = path.listFiles();
                for (int i = 0; i < list.length; i++  ){
                    listDirectory(list[i], files);
                }
            }
        }
    }

    /**
     * 重新载入
     */
    public void reload() {
        resourcesMap.clear();
        init("sql");
    }

    /**
     * 获取sql语句
     * @param clazName 类package全路径
     * @param methodName 方法名字
     * @return 返回配置的sql语句
     */
    public String getSQL(String clazName,String methodName) {
       Map<String,String> m= resourcesMap.get(clazName);
        return  m.get(methodName);
    }

    /**
     * 获取sql语句
     * @return
     */
    public static String getCurrentSql(){
        //获取调用调用此方法的上一级类
        String clazz= Thread.currentThread().getStackTrace()[2].getClassName();
        //获取调用getSQL方法的上一级方法
        String method= Thread.currentThread().getStackTrace()[2].getMethodName();
       return  SpringKit.getApplicationContext().getBean(SqlXmlKit.class).getSQL(clazz,method);
    }
}



