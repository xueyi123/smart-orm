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
import com.iih5.smartorm.dialect.DefaultDialect;
import com.iih5.smartorm.kit.SpringKit;
import com.iih5.smartorm.kit.StringKit;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.swing.*;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

/**
 * DbExecutor. Professional database query and update tool.
 */
public class DbExecutor {
    private static Map<String, DbExecutor> map = new HashMap<String, DbExecutor>();
    private static String defaultDataSource = null;
    private Object[] NULL_PARA_ARRAY = new Object[]{};
    public JdbcTemplate jdbc = null;
    /**
     * 选择使用数据库（默认选中第一个）
     * @param dataSource 在spring.xml里配置的jdbc dataSource beanId
     * @return 返回DbExecutor.
     */
    public static  DbExecutor use(String dataSource) {
        DbExecutor executor =map.get(dataSource);
        if (executor==null){
            executor=new DbExecutor();
            executor.jdbc= SpringKit.getJdbcTemplateByDataSource(dataSource);
            map.put(dataSource,executor);
        }
        return executor;
    }
    /**
     * 默认第一个dataSource
     * @return DbExecutor
     */
    public static DbExecutor use() {
        if (defaultDataSource==null){
            String[] dbs = SpringKit.getApplicationContext().getBeanNamesForType(ComboPooledDataSource.class);
            defaultDataSource = dbs[0];
        }
        DbExecutor executor =map.get(defaultDataSource);
        if (executor==null){
            executor =new DbExecutor();
            executor.jdbc= SpringKit.getJdbcTemplateByDataSource(defaultDataSource);
            map.put(defaultDataSource,executor);
        }
        return  executor;
    }

    /**
     * 返回JdbcTemplate
     * @param dataSource
     * @return
     */
    public  JdbcTemplate getJdbcTemplate(String dataSource) {
        DbExecutor executor =map.get(dataSource);
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
        return  getJdbcTemplate(defaultDataSource);
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
    <T> List<T> queryList(String sql, Object[] paras, final Class<T> model) throws Exception {
        final Set<String> columnMeta= new HashSet<String>();
        return jdbc.query(sql, paras, new RowMapper<T>() {
            public T mapRow(ResultSet rs, int rowNum) throws SQLException {
                try {
                    if (columnMeta.size()==0){
                        for (int i = 0; i <rs.getMetaData().getColumnCount() ; i++) {
                            String column= rs.getMetaData().getColumnLabel(i+1);
                            columnMeta.add(column);
                        }
                    }
                    Model mModel = (Model) model.newInstance();
                    Field[] fields = mModel.getClass().getFields();
                    if (0 < fields.length) {
                        for (Field f : fields) {
                            if (columnMeta.contains(f.getName())){
                                f.set(mModel,rs.getObject(f.getName()));
                            }
                        }
                    }else {
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
     * @param paras
     * @param model
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> List<T> findList(String sql, Object[] paras, final Class<T> model) throws Exception {
       return queryList(sql, paras, model);
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
     * 查找Model对象
     * @param sql
     * @param paras
     * @param model
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> T find(String sql, Object[] paras, final Class<T> model) throws Exception {
        List<T> result = findList(sql,paras,model);
        return result.size() > 0 ? result.get(0) : null;
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

    /**
     * 分页查询
     * @param model
     * @param pageNumber 第几页
     * @param pageSize 每一页的大小
     * @param sql 查询语句 (不能带limit,系统会自动带上)
     * @param paras 查询参数
     * @return the Page object
     */
    public  <T> Page<T> paginate(final  Class<T> model,int pageNumber, int pageSize, String sql,Object[] paras) throws Exception {
        String tableName = StringKit.toTableNameByModel(model);
        String countSQL= DefaultDialect.getDialect().forModelFindBy(tableName,"count(*)","");
        long size= findBasicObject(countSQL,Long.class);
        long totalRow=size;
        if (totalRow == 0) {
            return new Page<T>(new ArrayList<T>(0), pageNumber, pageSize, 0, 0);
        }
        int totalPage = (int) (totalRow / pageSize);
        if (totalRow % pageSize != 0) {
            totalPage++;
        }
        if (pageNumber > totalPage) {
            return new Page<T>(new ArrayList<T>(0), pageNumber, pageSize, totalPage, (int)totalRow);
        }

        int offset = pageSize * (pageNumber - 1);
        StringBuilder ssql = new StringBuilder();
        ssql.append(sql).append(" ");
        ssql.append(" limit ").append(offset).append(", ").append(pageSize);
        List<T> list = findList(ssql.toString(),paras,model);
        return new Page<T>(list, pageNumber, pageSize, totalPage, (int)totalRow);
    }


    /**
     * 获取Map格式列表(不包含attrs包裹属性)
     * @param sql
     * @return
     */
    public List<Map<String,Object>> findList(String sql,boolean isNotAttr){
        return jdbc.queryForList(sql);
    }

    /**
     * 获取Map格式列表(不包含attrs包裹属性)
     * @param sql
     * @param paras
     * @return
     */
    public List<Map<String,Object>> findList(String sql, Object[] paras,boolean isNotAttr){
        return jdbc.queryForList(sql,paras);
    }

    /**
     * 分页查询(不包含attrs包裹属性)
     * @param model
     * @param pageNumber 第几页
     * @param pageSize 每一页的大小
     * @param sql 查询语句 (不能带limit,系统会自动带上)
     * @param paras 查询参数
     * @return the Page object
     */
    public <T> Page<Map> paginate(final  Class<T> model,int pageNumber, int pageSize, String sql,Object[] paras,boolean isNotAttr) throws Exception {
        String tableName = StringKit.toTableNameByModel(model);
        String countSQL= DefaultDialect.getDialect().forModelFindBy(tableName,"count(*)","");
        long size= findBasicObject(countSQL,Long.class);
        long totalRow=size;
        if (totalRow == 0) {
            return new Page<Map>(new ArrayList<Map>(0), pageNumber, pageSize, 0, 0);
        }
        int totalPage = (int) (totalRow / pageSize);
        if (totalRow % pageSize != 0) {
            totalPage++;
        }
        if (pageNumber > totalPage) {
            return new Page<Map>(new ArrayList<Map>(0), pageNumber, pageSize, totalPage, (int)totalRow);
        }
        int offset = pageSize * (pageNumber - 1);
        StringBuilder ssql = new StringBuilder();
        ssql.append(sql).append(" ");
        ssql.append(" limit ").append(offset).append(", ").append(pageSize);
        List list = findList(ssql.toString(),paras,isNotAttr);
        return new Page<Map>(list, pageNumber, pageSize, totalPage, (int)totalRow);
    }
}



