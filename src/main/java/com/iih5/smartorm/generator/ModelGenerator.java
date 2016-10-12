package com.iih5.smartorm.generator;
/*
 * Copyright 2016 xueyi (1581249005@qq.com)
 *
 * The SmartORM Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

import com.alibaba.fastjson.JSON;
import com.iih5.smartorm.kit.StringKit;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ModelGenerator {
    //表是否有前缀
    public static boolean isHashPrefix = true;
    /**
     * 生成Model文件，输出目录与包名与 Model相同
     * @param dataSource 数据源名称（在spring.xml配置）
     * @param db 数据库名
     * @param modelPackageName model 包名
     * @param javaOutputDir   java 输出目录
     */
    public static  void  generator(String dataSource,String db, String modelPackageName, String javaOutputDir) throws Exception {
        List<TableMeta> tableMetaList= TableMetaTool.findTableMetaList(dataSource,db);
        for (TableMeta table:tableMetaList) {
            build(table,modelPackageName,javaOutputDir);
        }
    }
    /**
     * 生成Model文件，输出目录与包名与 Model相同
     *
     * @param db 数据库名
     * @param modelPackageName model 包名
     * @param javaOutputDir   java 输出目录
     */
    public static  void  generator(String db, String modelPackageName, String javaOutputDir) throws Exception {
        List<TableMeta> tableMetaList= TableMetaTool.findTableMetaList(null,db);
        for (TableMeta table:tableMetaList) {
            build(table,modelPackageName,javaOutputDir);
        }
    }
    private static void build(TableMeta tableMeta, String modelPackageName, String javaOutputDir)throws Exception {
        StringBuffer absoluteDir= new StringBuffer();
        absoluteDir.append(javaOutputDir);
        absoluteDir.append("/");
        absoluteDir.append(modelPackageName.replaceAll("\\.","/"));
        ModelBuilder builder = new ModelBuilder();
        String str = builder.doBuild(tableMeta, modelPackageName);
        writeToFile(str,tableMeta,absoluteDir.toString());
    }
    /**
     * 写入文件（如有重复，覆盖之前）
     */
    protected static void writeToFile(String content,TableMeta tableMeta,String outputDir) throws IOException {
        File dir = new File(outputDir);
        dir.mkdirs();
        String target = outputDir + File.separator + StringKit.firstCharToUpperCase(StringKit.toModelNameByTable(tableMeta.name)) + "Model.java";
        FileWriter fw = new FileWriter(target);
        try {
            fw.write(content);
        }
        finally {
            fw.close();
        }
    }
}