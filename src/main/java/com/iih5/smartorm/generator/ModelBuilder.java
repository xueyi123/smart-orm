package com.iih5.smartorm.generator;/*
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

public class ModelBuilder {

    private StringBuffer builder=null;
    private StringBuffer packageBuilder;
    private StringBuffer importBuilder;
    private StringBuffer classBuilder;
    private StringBuffer serialBuilder;
    private StringBuffer columnBuilder;

    public ModelBuilder(){
        builder = new StringBuffer();
        packageBuilder = new StringBuffer();
        classBuilder   = new StringBuffer();
        importBuilder  = new StringBuffer();
        serialBuilder  = new StringBuffer();
        columnBuilder  = new StringBuffer();
    }
    private void  join(){
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
    private ModelBuilder createPackage(String pack){
        packageBuilder.append("package "+pack+";");
        return (this);
    }
    private ModelBuilder createImport(String imp){
        importBuilder.append("import "+imp+";\n");
        return (this);
    }
    private ModelBuilder createClass(String clas){
        classBuilder.append("public class "+clas+" extends Model<"+clas+">{");
        return (this);
    }
    private ModelBuilder createSerialVersion(){
        serialBuilder.append("private static final long serialVersionUID = 1L;");
        return (this);
    }
    private ModelBuilder createColumn(String type, String column, String comment){
        columnBuilder.append("//"+comment+"\n");
        columnBuilder.append("public "+type+" "+column+";\n");
        return (this);
    }
    public String  doBuild(TableMeta tableMeta,String packageName){
        createPackage(packageName);
        createImport("com.iih5.smartorm.model.Model");
        createClass(StringKit.toModelNameByTable(tableMeta.name));
        for (ColumnMeta columnMeta:tableMeta.columnMetas) {
            String javaType= JavaType.getJavaTypeByDataType(columnMeta.dataType);
            if (javaType==null){
                throw new NullPointerException("找不到 "+columnMeta.dataType+"对应的JavaType");
            }
            createColumn(javaType,columnMeta.name,columnMeta.comment);
        }
        join();
        return  builder.toString();
    }

}
