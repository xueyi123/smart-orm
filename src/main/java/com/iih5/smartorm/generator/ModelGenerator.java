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

import com.iih5.smartorm.kit.StringKit;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ModelGenerator {

    /**
     * 1 获取TableMeta List
     * 2 生成 java
     * 3 保存 文件
     *
     *
     *
     *
     */

    /**
     * 构造 Generator，Model文件，输出目录与包名与 Model相同
     *
     * @param modelPackageName model 包名
     * @param modelOutputDir   model 输出目录
     */
    public static  void  generator(TableMeta tableMeta, String modelPackageName, String modelOutputDir) throws IOException {
        StringBuffer absoluteDir= new StringBuffer();
        absoluteDir.append(modelOutputDir);
        ModelBuilder builder = new ModelBuilder();
        String[] arr= modelPackageName.split(".");
        for (String pack:arr) {
            absoluteDir.append("/");
            absoluteDir.append(pack);
        }
        String str = builder.doBuild(tableMeta, absoluteDir.toString());
        writeToFile(str,tableMeta,modelOutputDir);
    }
    /**
     * 写入文件（如有重复，覆盖之前）
     */
    protected static void writeToFile(String content,TableMeta tableMeta,String outputDir) throws IOException {
        File dir = new File(outputDir);
        if (!dir.exists())
            dir.mkdirs();

        String target = outputDir + File.separator + StringKit.toModelNameByTable(tableMeta.name) + ".java";
        FileWriter fw = new FileWriter(target);
        try {
            fw.write(content);
        }
        finally {
            fw.close();
        }
    }

}