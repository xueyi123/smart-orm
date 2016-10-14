package com.iih5.smartorm.generator;
import com.alibaba.fastjson.JSON;
import com.iih5.smartorm.model.Db;

import java.sql.*;
import java.util.*;

public class TableMetaUtil {

    /**
     * 从db获取库表和字段信息
     * @param dataSource
     * @return
     * @throws Exception
     */
    public static List<TableMeta> findTableMetaList(String dataSource) throws Exception{
        List<TableMeta> tableList = new ArrayList<TableMeta>();
        List<String> sets = Db.use(dataSource).findBasicObjectList("show tables ;", String.class);
        for (String name:sets) {
            String sql = " show full columns from "+name+" ;";
            TableMeta meta = new TableMeta();
            meta.name = name;
            Map<String,String> javaTypeMap = toJavaTypeMap(name);
            List<Map<String,Object>> mpList = Db.findList(sql,true);
            for (Map<String,Object> mp:mpList) {
                ColumnMeta columnMeta= new ColumnMeta();
                columnMeta.name = (String) mp.get("Field");
                columnMeta.comment = (String) mp.get("Comment");
                columnMeta.dataType = javaTypeMap.get(columnMeta.name);
                meta.columnMetas.add(columnMeta);
            }
            tableList.add(meta);
        }
        return tableList;
    }
    /**
     * 组合TableMeta
     * @param tableName
     * @return
     */
    private static  Map<String,String> toJavaTypeMap(String tableName)  {
        Connection connection = null;
        Statement stm = null;
        ResultSet rs = null;
        try {
            Map<String,String> javaType = new HashMap<String, String>();
            String sql="select * from "+tableName+" where 1=2 ";
            connection = Db.getJdbcTemplate().getDataSource().getConnection();
            stm =  connection.createStatement();
            rs = stm.executeQuery(sql);
            ResultSetMetaData rmd = rs.getMetaData();
            for (int i=1; i<= rmd.getColumnCount(); i++) {
                javaType.put(rmd.getColumnLabel(i),rmd.getColumnClassName(i));
            }
            return javaType;
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                connection.close();
                rs.close();
                stm.close();
            } catch (SQLException e) {
            }
        }
        return null;
    }
}
