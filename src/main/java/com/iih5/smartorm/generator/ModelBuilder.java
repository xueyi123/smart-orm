package com.iih5.smartorm.generator;

import com.iih5.smartorm.kit.StringKit;

public class ModelBuilder {

    private StringBuffer builder=null;
    private StringBuffer packageBuilder;
    private StringBuffer importBuilder;
    private StringBuffer classBuilder;
    private StringBuffer constructBuilder;
    private StringBuffer columnBuilder;
    private StringBuffer setMethodBuilder;
    private StringBuffer getMethodBuilder;

    public ModelBuilder(){
        builder = new StringBuffer();
        packageBuilder = new StringBuffer();
        classBuilder   = new StringBuffer();
        importBuilder  = new StringBuffer();
         constructBuilder  = new StringBuffer();
        columnBuilder  = new StringBuffer();
        setMethodBuilder=new StringBuffer();
        getMethodBuilder=new StringBuffer();
    }
    private void  join(){
        builder.append(packageBuilder);
        builder.append("\n\n");
        builder.append(importBuilder);
        builder.append("\n");
        builder.append(classBuilder);
        builder.append("\n");
        builder.append( constructBuilder);
        builder.append("\n");
        builder.append(columnBuilder);
        builder.append("\n");
        builder.append(setMethodBuilder);
        builder.append("\n");
        builder.append(getMethodBuilder);
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
        classBuilder.append("public class " + clas + " extends Model {");
        return (this);
    }
    private ModelBuilder createConstruct(String clas,String tableName){
        constructBuilder.append("    public transient String TABLE = \""+tableName+"\";\n");
        constructBuilder.append("    public " + clas + "(){");
        constructBuilder.append("\n       super.TABLE = this.TABLE;");
        constructBuilder.append("\n    }");
        return (this);
    }
    private ModelBuilder createColumn(String type, String column, String comment){
        columnBuilder.append("    //"+comment+"\n");
        columnBuilder.append("    private "+type+" "+StringKit.firstCharToLowerCase(StringKit.toCamelCaseName(column))+";\n");
        return (this);
    }
    private ModelBuilder createSetMethod(Object type, String column){
        setMethodBuilder.append("    public void set");
        setMethodBuilder.append(StringKit.firstCharToUpperCase(StringKit.toCamelCaseName(column))+"("+type+" "+StringKit.toCamelCaseName(column)+") { \n");
        setMethodBuilder.append("        this."+StringKit.toCamelCaseName(column)+" = "+StringKit.toCamelCaseName(column)+"; \n");
        setMethodBuilder.append("    }\n\n");
        return (this);
    }
    private ModelBuilder createSetCalculateMethod(String type, String column) {
        if (type.contains("Integer") || type.contains("Long") || type.contains("Double") || type.contains("Float")) {
            setMethodBuilder.append("    public void set");
            setMethodBuilder.append(StringKit.firstCharToUpperCase(StringKit.toCamelCaseName(column)) + "(" + type + " " + StringKit.toCamelCaseName(column) + ",String calculate" + ") { \n");
            setMethodBuilder.append("        this." + StringKit.toCamelCaseName(column) + " = " + StringKit.toCamelCaseName(column) + "; \n");
            setMethodBuilder.append("        this.addCalPrefix( \""+ StringKit.toCamelCaseName(column)+"\",calculate)"+";\n");
            setMethodBuilder.append("    }\n\n");
        }
        return (this);
    }

    private ModelBuilder createGetMethod(Object type, String column){
        setMethodBuilder.append("    public "+type+" "+"get");
        setMethodBuilder.append(StringKit.firstCharToUpperCase(StringKit.toCamelCaseName(column))+"() { \n");
        setMethodBuilder.append("        return "+StringKit.toCamelCaseName(column)+";\n");
        setMethodBuilder.append("    }\n\n");
        return (this);
    }
    public String  doBuild(TableMeta tableMeta,String packageName){
        createPackage(packageName);
        createImport("com.iih5.smartorm.model.Model");
        String className = StringKit.firstCharToUpperCase(StringKit.toModelNameByTable(tableMeta.name)+"Model") ;
        createClass(className);
        createConstruct(className,tableMeta.name);
        JavaType javaTypeM = new JavaType();
        for (ColumnMeta columnMeta:tableMeta.columnMetas) {
            String javaType= javaTypeM.getType(columnMeta.dataType);
            if (javaType==null){
                throw new NullPointerException("找不到 "+columnMeta.dataType+"对应的JavaType");
            }
            if (JavaKeyword.contains(columnMeta.name)){
                throw new IllegalArgumentException("非法参数名:"+columnMeta.name);
            }
            createColumn(javaType,columnMeta.name,columnMeta.comment);
            createSetMethod(javaType,columnMeta.name);
            createSetCalculateMethod(javaType,columnMeta.name);
            createGetMethod(javaType,columnMeta.name);
        }
        join();
        return  builder.toString();
    }

}
