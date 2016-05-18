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

package com.iih5.smartorm.dialect;

import java.util.*;
import java.util.Map.Entry;
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
        sql.append(" where ");
        sql.append(conditions);
        return sql.toString();
    }

    public String deleteByCondition(String tableName,String conditions) {
        StringBuilder sql = new StringBuilder(45);
        sql.append("delete from ");
        sql.append(tableName);
        sql.append(" where ");
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
        List<Object> paras= new ArrayList<Object>();
        sql.append("update ").append(tableName).append(" set ");
        Pattern p= Pattern.compile("[0-9\\+\\-\\*\\/\\(\\)]*");
        for (Entry<String, Object> e : attrs.entrySet()) {
            String colName = e.getKey();
            if (modifyFlag.contains(colName)) {
                if (paras.size() > 0) {
                    sql.append(", ");
                }
                if (e.getValue() instanceof String){
                    String d=String.valueOf(e.getValue());
                    String dr= ((String) e.getValue()).replace(colName,"");
                    Matcher m = p.matcher(dr);
                    if (m.matches()){
                        sql.append(" ").append(colName).append(" = "+e.getValue());
                    }else {
                        sql.append(" ").append(colName).append(" = '"+e.getValue()+"'");
                    }
                }else {
                    sql.append(" ").append(colName).append(" = "+e.getValue());
                }
                paras.add(colName);
            }
        }
        sql.append(" where ");
        sql.append(conditions);
    }

    public static void main(String[] args) {
        String regex="[0-9\\+\\-\\*\\/\\(\\)]*";
        String d="value100";
        Pattern p= Pattern.compile(regex);//"[+-/*]"
      //  ②建造一个匹配器
        Matcher m = p.matcher(d);
      //  ③进行判断，得到结果
        boolean b = m.find();
        System.out.println(b);

    }



}
