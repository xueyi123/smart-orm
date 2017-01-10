package com.iih5.smartorm.model;

import com.iih5.smartorm.kit.SqlXmlKit;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

public class DB {
    private static DbExecutor defaultExecutor = null;
    static {
        defaultExecutor = DbExecutor.use();
    }
    /**
     * 选择使用数据库（默认选中第一个）
     * @param dataSource 在spring.xml里配置的jdbc dataSource
     * @return
     */
    public static  DbExecutor use(String dataSource){
        if (dataSource == null){
            return defaultExecutor;
        }
        return DbExecutor.use(dataSource);
    }

    /**
     * 获取配置的 JdbcTemplate
     * @param dataSource
     * @return
     */
    public static JdbcTemplate getJdbcTemplate(String dataSource){
        return  use(dataSource).getJdbcTemplate();
    }

    /**
     * 获取配置的 JdbcTemplate
     * @return
     */
    public static JdbcTemplate getJdbcTemplate(){
        return  defaultExecutor.getJdbcTemplate();
    }


    public static TB model(Model model){
        TB executor = new TB();
        executor.table=model.tableName();
        executor.model=model;
        return executor;
    }

    /**
     * 查找claszz的bean对象
     * @param sql sql语句 参数用?代替
     * @param paras 参数
     * @param clazz 返回对象
     * @param <T> 返回对象
     * @return
     * @
     */
    public static <T> T find(String sql, Object[] paras,  Class<T> clazz)  {

        return defaultExecutor.find(sql,paras,clazz);
    }
    /**
     * 查找claszz的bean对象
     * @param sql sql语句 参数用?代替
     * @param clazz 返回对象
     * @param <T> 返回对象
     * @return
     * @
     */
    public static <T> T find(String sql,  Class<T> clazz)  {
        return defaultExecutor.find(sql,new Object[]{},clazz);
    }

    /**
     * 查找对象
     * @param fileName sql的xml文件名
     * @param method xml 里定义的 method
     * @param params 动态参数，map或者bean的形式传递进去
     * @param clazz 查找的对象
     * @param <T>
     * @return 返回对象
     */
    public static <T> T find(String fileName,String method,Object params , Class<T> clazz)  {
        String sql =  SqlXmlKit.getSQL(fileName,method);
        String result = SqlXmlKit.autoAssembleSQL(sql,params);
        return defaultExecutor.find(result,new Object[]{},clazz);
    }
    /**
     * 查找Model对象列表
     * @param sql
     * @param paras
     * @param clazz
     * @param <T>
     * @return
     * @
     */
    public static <T> List<T> findList(String sql, Object[] paras,  Class<T> clazz)  {
        return  defaultExecutor.findList(sql,paras,clazz);
    }
    /**
     * 查找Model对象列表
     * @param sql  sql语句 参数用?代替
     * @param clazz
     * @param <T> 返回对象
     * @return
     * @
     */
    public static <T> List<T> findList(String sql, Class<T> clazz)  {
        return  defaultExecutor.findList(sql,new Object[]{},clazz);
    }

    /**
     * 查找对象列表
     * @param fileName sql的xml文件名
     * @param method xml 里定义的 method
     * @param params 动态参数，map或者bean的形式传递进去
     * @param clazz 查找的对象
     * @param <T>
     * @return 返回对象列表
     */
    public static <T> List<T> findList(String fileName,String method,Object params, Class<T> clazz)  {
        String sql =  SqlXmlKit.getSQL(fileName,method);
        String result = SqlXmlKit.autoAssembleSQL(sql,params);
        return  defaultExecutor.findList(result,new Object[]{},clazz);
    }
    /**
     * 获取Map格式列表
     * @param sql sql语句 参数用?代替
     * @param paras 参数
     * @return  返回  List<Map<String,Object>>
     */
    public static List<Map<String,Object>> findList(String sql, Object[] paras){
        return defaultExecutor.findList(sql, paras);
    }

    /**
     * 更新数据对象（update or insert,delete）
     * @param sql 参数用?代替
     * @param paras 参数
     * @return
     * @throws DataAccessException
     */
    public static int update(String sql, Object[] paras) throws DataAccessException {
        return defaultExecutor.update(sql,paras);
    }

    /**
     * 批量更新数据对象（可执行update，insert,delete三种语句）
     * @param sql sql语句 参数用?代替
     * @param batchArgs 批量插入的参数
     * @return
     */
    public static int[] batchUpdate(String sql, List<Object[]> batchArgs) {
        return defaultExecutor.batchUpdate(sql,batchArgs);
    }
    /**
     * sql语句执行的基础命令，没有返回值（可以执行DML操作和存储过程）
     * @param sql sql sql语句 参数用?代替
     */
    public static void execute(String sql){
        defaultExecutor.execute(sql);
    }
}
