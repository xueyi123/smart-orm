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
import com.iih5.smartorm.model.Db;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AGenerator {

    private StringBuffer builder=null;
    private StringBuffer packageBuilder;
    private StringBuffer importBuilder;
    private StringBuffer classBuilder;
    private StringBuffer serialBuilder;
    private StringBuffer columnBuilder;
    public void  init(){
        if (builder==null) {
            builder = new StringBuffer();
            packageBuilder = new StringBuffer();
            classBuilder   = new StringBuffer();
            importBuilder  = new StringBuffer();
            serialBuilder  = new StringBuffer();
            columnBuilder  = new StringBuffer();
            builder.append(packageBuilder);
            builder.append("\n\n");
            builder.append(importBuilder);
            builder.append("\n");
            builder.append(classBuilder);
            builder.append("\n");
            builder.append(serialBuilder);
            builder.append("\n");
            builder.append(columnBuilder);
            builder.append("\n");
            builder.append("\n");
            builder.append("}\n");
        }
    }
    private AGenerator createPackage(String pack){
        packageBuilder.append("package "+pack+";");
        return (this);
    }
    private AGenerator createImport(String imp){
        importBuilder.append("import "+imp+";\n");
        return (this);
    }
    private AGenerator createClass(String clas){
        classBuilder.append("public class "+clas+" extends Model<"+clas+">{");
        return (this);
    }
    private AGenerator createSerialVersion(){
        serialBuilder.append("private static final long serialVersionUID = 1L;");
        return (this);
    }
    private AGenerator createColumn(String type,String column,String comment){
        columnBuilder.append("//"+comment+"\n");
        columnBuilder.append("public "+type+" "+column+";\n");
        return (this);
    }

    public  String  createModel(String pack,TableMetaTest tableMeta){
        createPackage(pack);
        createClass(tableMeta.name);
//        for (ColumnMetaTest columnMeta:tableMeta.columnMetas) {
//
//            columnMeta.dataType;
//
//            createColumn(,columnMeta.name,columnMeta.comment);
//        }
//        createImport();
//
//        createSerialVersion();
//        createColumn();
        return  null;
    }



}