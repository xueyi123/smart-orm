package com.iih5.smartorm.model;

import com.iih5.smartorm.kit.SpringKit;
import com.iih5.smartorm.kit.StringKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * DbExecutor. Professional database query and update tool.
 */
public class DbExecutor {
    Logger logger = LoggerFactory.getLogger(DbExecutor.class);
    private static Map<String, DbExecutor> map = new HashMap<String, DbExecutor>();
    private static String defaultDataSource = null;
    public JdbcTemplate jdbc = null;

    /**
     * 选择使用数据库（默认选中第一个）
     *
     * @param dataSource 在spring.xml里配置的jdbc dataSource beanId
     * @return 返回DbExecutor.
     */
    public static DbExecutor use(String dataSource) {
        DbExecutor executor = map.get(dataSource);
        if (executor == null) {
            executor = new DbExecutor();
            executor.jdbc = SpringKit.getJdbcTemplateByDataSource(dataSource);
            map.put(dataSource, executor);
        }
        return executor;
    }

    /**
     * 默认第一个dataSource
     *
     * @return DbExecutor
     */
    public static DbExecutor use() {
        if (defaultDataSource == null) {
            String[] dbs = SpringKit.getApplicationContext().getBeanNamesForType(DataSource.class);
            defaultDataSource = dbs[0];
        }
        DbExecutor executor = map.get(defaultDataSource);
        if (executor == null) {
            executor = new DbExecutor();
            executor.jdbc = SpringKit.getJdbcTemplateByDataSource(defaultDataSource);
            map.put(defaultDataSource, executor);
        }
        return executor;
    }

    /**
     * 返回JdbcTemplate
     *
     * @param dataSource
     * @return
     */
    public JdbcTemplate getJdbcTemplate(String dataSource) {
        DbExecutor executor = map.get(dataSource);
        if (executor != null) {
            return executor.jdbc;
        }
        return null;
    }

    /**
     * 返回JdbcTemplate
     *
     * @param
     * @return
     */
    public JdbcTemplate getJdbcTemplate() {
        return getJdbcTemplate(defaultDataSource);
    }

    private  <T> List<T> queryList(String sql, Object[] paras, final Class<T> claszz) {
        if (StringKit.isBaseDataType(claszz)){
            return jdbc.queryForList(sql,paras,claszz);
        }
        final Set<String> columnMeta = new HashSet<String>();
        final Map<String, String> fieldMap = new HashMap<String, String>();
        return jdbc.query(sql, paras, new RowMapper<T>() {
            public T mapRow(ResultSet rs, int rowNum) throws SQLException {
                try {
                    if (columnMeta.size() <= 0) {
                        for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                            String column = rs.getMetaData().getColumnLabel(i + 1);
                            columnMeta.add(column);
                        }
                    }
                    Object mModel = claszz.newInstance();
                    Field[] fields = mModel.getClass().getDeclaredFields();
                    if (fields.length > 0) {
                        for (Field field : fields) {
                            if (field.getName().equals("TABLE")) continue;
                            String column = fieldMap.get(field.getName());
                            if (column == null) {
                                column = StringKit.toUnderscoreName(field.getName());
                                fieldMap.put(field.getName(), column);
                            }
                            if (columnMeta.contains(column)) {
                                Object value = rs.getObject(column);
                                PropertyDescriptor pd = new PropertyDescriptor(field.getName(), mModel.getClass());
                                Method method = pd.getWriteMethod();
                                method.invoke(mModel, value);
                            }
                        }
                    }
                    return (T) mModel;
                } catch (Exception e) {
                    logger.error("异常",e);
                    throw new DataException(e.getMessage());
                }
            }
        });
    }

    /**
     * 查找Model对象列表
     *
     * @param sql    sql语句 参数用?代替
     * @param paras  参数
     * @param claszz
     * @param <T>    返回对象
     * @return
     * @
     */
    public <T> List<T> findList(String sql, Object[] paras, Class<T> claszz) {
        return queryList(sql, paras, claszz);
    }

    /**
     * 获取Map格式列表
     *
     * @param sql   sql语句 参数用?代替
     * @param paras 参数
     * @return 返回  List<Map<String,Object>>
     */
    public List<Map<String, Object>> findList(String sql, Object[] paras) {
        return jdbc.queryForList(sql, paras);
    }

    /**
     * 查找claszz的bean对象
     *
     * @param sql    sql语句 参数用?代替
     * @param paras  参数
     * @param clazz 返回对象
     * @param <T>    返回对象
     * @return
     * @
     */
    public <T> T find(String sql, Object[] paras, Class<T> clazz) {
        List<T> result = findList(sql, paras, clazz);
        if (result.size() > 1) {
            throw new DataException("返回多于1条数据");
        }
        if (result.size() == 0) {
            return null;
        }
        return result.get(0);
    }

    /**
     * 更新数据对象（update or insert,delete）
     *
     * @param sql   参数用?代替
     * @param paras 参数
     * @return
     * @throws DataAccessException
     */
    public int update(String sql, Object[] paras) throws DataAccessException {
        return jdbc.update(sql, paras);
    }

    /**
     * 批量更新数据对象（可执行update，insert,delete三种语句）
     *
     * @param sql       sql语句 参数用?代替
     * @param batchArgs 批量插入的参数
     * @return
     */
    public int[] batchUpdate(String sql, List<Object[]> batchArgs) {
        return jdbc.batchUpdate(sql, batchArgs);
    }

    /**
     * sql语句执行的基础命令，没有返回值（可以执行DML操作和存储过程）
     *
     * @param sql sql sql语句 参数用?代替
     */
    public void execute(String sql) {
        jdbc.execute(sql);
    }
}








