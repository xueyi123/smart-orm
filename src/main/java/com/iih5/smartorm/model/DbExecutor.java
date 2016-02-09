package com.iih5.smartorm.model;
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
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * DbExecutor. Professional database query and update tool.
 */
public class DbExecutor {
    private static Map<String, DbExecutor> map = new HashMap<String, DbExecutor>();
    private static String defaultJdbcBeanName = "";
    private Object[] NULL_PARA_ARRAY = new Object[]{};
    public JdbcTemplate jdbc = null;
    /**
     * 选择使用数据库（默认选中第一个）
     * @param jdbcBeanId 在spring.xml里配置的jdbc template beanId
     * @return 返回JdbcTemplate
     */
    public static  DbExecutor use(String jdbcBeanId) {
        DbExecutor executor =map.get(jdbcBeanId);
        if (executor==null){
            executor=new DbExecutor();
            executor.jdbc=SpringContext.getInstace().getBean(jdbcBeanId);
            map.put(jdbcBeanId,executor);
        }
        return executor;
    }
    /**
     * 默认返回第一个JdbcTemplate
     * @return 返回JdbcTemplate
     */
    public static DbExecutor use() {
        String[] dbs = SpringContext.getInstace().getCtx().getBeanNamesForType(JdbcTemplate.class);
        DbExecutor executor=null;
        if (dbs != null && dbs.length > 0) {
            defaultJdbcBeanName = dbs[0];
            executor =new DbExecutor();
            executor.jdbc=SpringContext.getInstace().getBean(defaultJdbcBeanName);
            map.put(defaultJdbcBeanName,executor);
        }
        return  executor;
    }

    /**
     * 返回JdbcTemplate
     * @param jdbcBeanId
     * @return
     */
    public  JdbcTemplate getJdbcTemplate(String jdbcBeanId) {
        DbExecutor executor =map.get(jdbcBeanId);
        if (executor!=null){
            return executor.jdbc;
        }
        return  null;
    }
    /**
     * 返回JdbcTemplate
     * @param
     * @return
     */
    public  JdbcTemplate getJdbcTemplate() {
        return  getJdbcTemplate(defaultJdbcBeanName);
    }

    /**
     * 查找Model对象
     * @param sql
     * @param paras
     * @param model
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> T find(String sql, Object[] paras, final Class<T> model) throws Exception {
        List<T> list = jdbc.query(sql, paras, new RowMapper<T>() {
            public T mapRow(ResultSet rs, int rowNum) throws SQLException {
                try {
                    Model mModel = (Model) model.newInstance();
                    Field[] fields = mModel.getClass().getFields();
                    if (0 < fields.length) for (Field f : fields) {
                        f.set(mModel, rs.getObject(f.getName()));
                    }
                    else {
                        ResultSetMetaData rad = rs.getMetaData();
                        int columnCount = rad.getColumnCount();
                        Map<String, Object> attrs = mModel.getAttrs();
                        for (int i = 1; i <= columnCount; i++) {
                            Object value = rs.getObject(i);
                            attrs.put(rad.getColumnName(i), value);
                        }
                    }
                    return (T) mModel;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
        return (T) list.get(0);
    }
    /**
     * 查找Model对象
     * @param sql
     * @param model
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> T find(String sql, final Class<T> model) throws Exception {
        return find(sql,NULL_PARA_ARRAY,model);
    }
    /**
     * 查找Model对象列表
     * @param sql
     * @param paras
     * @param model
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> List<T> findList(String sql, Object[] paras, final Class<T> model) throws Exception {
        return jdbc.query(sql, paras, new RowMapper<T>() {
            public T mapRow(ResultSet rs, int rowNum) throws SQLException {
                try {
                    Model mModel = (Model) model.newInstance();
                    Field[] fields = mModel.getClass().getFields();
                    if (0 < fields.length) {
                        for (Field f : fields) {
                            f.set(mModel, rs.getObject(f.getName()));
                        }
                    } else {
                        ResultSetMetaData rad = rs.getMetaData();
                        int columnCount = rad.getColumnCount();
                        Map<String, Object> attrs = mModel.getAttrs();
                        for (int i = 1; i <= columnCount; i++) {
                            Object value = rs.getObject(i);
                            attrs.put(rad.getColumnName(i), value);
                        }
                    }
                    return (T) mModel;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
    }
    /**
     * 查找Model对象列表
     * @param sql
     * @param model
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> List<T> findList(String sql, final Class<T> model) throws Exception {
        return  findList(sql,NULL_PARA_ARRAY,model);
    }
    /**
     * 查找基础对象
     * @param sql
     * @param paras
     * @param classType
     * @param <T>
     * @return
     */
    public <T> T findBasicObject(String sql, Object[] paras, Class<T> classType) {
        return jdbc.queryForObject(sql, paras, classType);
    }

    /**
     * 查找基础对象
     * @param sql
     * @param classType
     * @param <T>
     * @return
     */
    public <T> T findBasicObject(String sql, Class<T> classType) {
        return jdbc.queryForObject(sql, NULL_PARA_ARRAY, classType);
    }

    /**
     * 查找基础对象列表
     * @param sql
     * @param paras
     * @param classType
     * @param <T>
     * @return
     */
    public <T> List<T> findBasicObjectList(String sql, Object[] paras, Class<T> classType) {
        return jdbc.queryForList(sql, paras, classType);
    }

    /**
     * 查找基础对象列表
     * @param sql
     * @param classType
     * @param <T>
     * @return
     */
    public <T> List<T> findBasicObjectList(String sql, Class<T> classType) {
        return jdbc.queryForList(sql, NULL_PARA_ARRAY, classType);
    }

    /**
     * 更新数据对象（update or insert,delete）
     * @param sql
     * @param paras
     * @return
     * @throws DataAccessException
     */
    public int update(String sql, Object[] paras) throws DataAccessException {
        return jdbc.update(sql, paras);
    }

    /**
     * 批量更新数据对象（update or insert,delete
     * @param sql
     * @param batchArgs
     * @return
     */
    public int[] batchUpdate(String sql, List<Object[]> batchArgs) {
        return jdbc.batchUpdate(sql, batchArgs);
    }



}



