
package com.iih5.smartorm.dialect;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MysqlDialect implements Dialect {
    public String forModelFindBy(String tableName,String columns,String conditions) {
        StringBuilder sql = new StringBuilder("select ");
        columns = columns.trim();
        if ("*".equals(columns)) {
            sql.append("*");
        }
        else {
            String[] arr = columns.split(",");
            for (int i=0; i<arr.length; i++) {
                if (i > 0) {
                    sql.append(",");
                }
                sql.append(" ").append(arr[i].trim()).append(" ");
            }
        }
        sql.append(" from ");
        sql.append(tableName);
        if (conditions==null || conditions.equals("")){
            return  sql.toString();
        }
        sql.append(" where 1=1 ");
        sql.append(conditions);
        return sql.toString();
    }

    public String deleteByCondition(String tableName,String conditions) {
        StringBuilder sql = new StringBuilder(45);
        sql.append("delete from ");
        sql.append(tableName);
        sql.append(" where 1=1 ");
        sql.append(conditions);
        return sql.toString();
    }
    public void forModelSave(String tableName, Map<String, Object> attrs, StringBuilder sql, List<Object> paras) {
        sql.append("insert into ").append(tableName).append("(");
        StringBuilder temp = new StringBuilder(") values(");
        for (Entry<String, Object> e: attrs.entrySet()) {
            String colName = e.getKey();

            if (paras.size() > 0) {
                sql.append(", ");
                temp.append(", ");
            }
            sql.append(" ").append(colName).append(" ");
            temp.append("?");
            paras.add(e.getValue());
        }
        sql.append(temp.toString()).append(")");
    }

    public void forModelUpdate(String tableName,String conditions, Map<String, Object> attrs, Set<String> modifyFlag, StringBuilder sql) {
        boolean isFirst = true;
        sql.append("update ").append(tableName).append(" set ");
        Pattern p= Pattern.compile("[0-9\\+\\-\\*\\/\\(\\)]*");
        for (Entry<String, Object> e : attrs.entrySet()) {
            String property = e.getKey();
            if (modifyFlag.contains(property)) {
                if (!isFirst){
                    sql.append(", ");
                }
                if (e.getValue() instanceof String ){
                    String dr= ((String) e.getValue()).replace(property,"");
                    Matcher m = p.matcher(dr);
                    if (m.matches()){
                        sql.append(" ").append( property).append(" = "+e.getValue());
                    }else {
                        sql.append(" ").append( property).append(" = '"+e.getValue()+"'");
                    }
                }else {
                    if (String.valueOf(e.getValue()).contains(":")){
                        sql.append(" ").append( property).append(" = '"+e.getValue()+"'");
                    }else {
                        sql.append(" ").append( property).append(" = "+e.getValue());
                    }
                }
                isFirst=false;
            }
        }
        sql.append(" where 1=1 ");
        sql.append(conditions);
    }
}
