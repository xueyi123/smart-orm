package com.iih5.smartorm.generator;

import com.iih5.smartorm.model.DB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TableMetaUtil {
    static Logger logger = LoggerFactory.getLogger(TableMetaUtil.class);
    /**
     * 从db获取库表和字段信息
     * @param dataSource
     * @return
     * @throws Exception
     */
    public static List<TableMeta> findTableMetaList(String dataSource) throws Exception{
        List<TableMeta> tableList = new ArrayList<TableMeta>();
        List<String> sets = null;
        if (dataSource !=null){
            sets = DB.use(dataSource).findList("show tables ;",new Object[]{} ,String.class);
        }else {
            sets =  DB.findList("show tables ;",new Object[]{} ,String.class);
        }

        for (String name:sets) {
            String sql = " show full columns from "+name+" ;";
            TableMeta meta = new TableMeta();
            meta.name = name;
            Map<String,String> javaTypeMap = toJavaTypeMap(name);
            List<Map<String,Object>> mpList = DB.use(dataSource).findList(sql,new Object[]{});
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
            connection = DB.getJdbcTemplate().getDataSource().getConnection();
            stm =  connection.createStatement();
            rs = stm.executeQuery(sql);
            ResultSetMetaData rmd = rs.getMetaData();
            for (int i=1; i<= rmd.getColumnCount(); i++) {
                javaType.put(rmd.getColumnLabel(i),rmd.getColumnClassName(i));
            }
            return javaType;
        }catch (Exception e){
            logger.error("异常",e);
        }finally {
            try {
                connection.close();
                rs.close();
                stm.close();
            } catch (SQLException e) {
                logger.error("异常",e);
            }
        }
        return null;
    }
}
