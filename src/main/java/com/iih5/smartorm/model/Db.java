package com.iih5.smartorm.model;/*
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
import java.util.List;
import java.util.Map;

public class Db {
    private static DbExecutor defaultExecutor = null;
    private static  String DbNamePrefix="t_";
    static {
        defaultExecutor = DbExecutor.use();
    }
    /**
     * 选择使用数据库（默认选中第一个）
     * @param jdbcBeanId 在spring.xml里配置的jdbc template beanId
     * @return
     */
    public static  DbExecutor use(String jdbcBeanId){
        return DbExecutor.use(jdbcBeanId);
    }

    /**
     * 获取配置的 JdbcTemplate
     * @param jdbcBeanId
     * @return
     */
    public static JdbcTemplate getJdbcTemplate(String jdbcBeanId){
        return  use(jdbcBeanId).getJdbcTemplate();
    }

    /**
     * 获取配置的 JdbcTemplate
     * @return
     */
    public static JdbcTemplate getJdbcTemplate(){
        return  defaultExecutor.getJdbcTemplate();
    }
    /***
     * 设置数据库表前缀
     * @param prefix
     */
    public static  void setDbNamePrefix(String prefix){
        DbNamePrefix=prefix;
    }

    /**
     * 获取数据库表前缀(默认 t_ )
     * @return
     */
    public  static  String getDbNamePrefix(){
        return  DbNamePrefix;
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
    public static <T> T find(String sql, Object[] paras, final Class<T> model) throws Exception {

        return defaultExecutor.find(sql,paras,model);
    }
    /**
     * 查找Model对象
     * @param sql
     * @param model
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> T find(String sql, final Class<T> model) throws Exception {
        return defaultExecutor.find(sql,model);
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
    public static <T> List<T> findList(String sql, Object[] paras, final Class<T> model) throws Exception {
        return  defaultExecutor.findList(sql,paras,model);
    }
    /**
     * 查找Model对象列表
     * @param sql
     * @param model
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> List<T> findList(String sql,final Class<T> model) throws Exception {
        return  defaultExecutor.findList(sql,model);
    }
    /**
     * 查找基础对象
     * @param sql
     * @param paras
     * @param classType
     * @param <T>
     * @return
     */
    public static <T> T findBasicObject(String sql, Object[] paras, Class<T> classType) {
        return defaultExecutor.findBasicObject(sql,paras,classType);
    }

    /**
     * 查找基础对象
     * @param sql
     * @param classType
     * @param <T>
     * @return
     */
    public static <T> T findBasicObject(String sql, Class<T> classType) {
        return defaultExecutor.findBasicObject(sql,classType);
    }

    /**
     * 查找基础对象列表
     * @param sql
     * @param paras
     * @param classType
     * @param <T>
     * @return
     */
    public static <T> List<T> findBasicObjectList(String sql, Object[] paras, Class<T> classType) {
        return defaultExecutor.findBasicObjectList(sql,paras,classType);
    }

    /**
     * 查找基础对象列表
     * @param sql
     * @param classType
     * @param <T>
     * @return
     */
    public static <T> List<T> findBasicObjectList(String sql, Class<T> classType) {
        return defaultExecutor.findBasicObjectList(sql,classType);
    }

    /**
     * 更新数据对象（update or insert,delete）
     * @param sql
     * @param paras
     * @return
     * @throws DataAccessException
     */
    public static int update(String sql, Object[] paras) throws DataAccessException {
        return defaultExecutor.update(sql,paras);
    }

    /**
     * 批量更新数据对象（update or insert,delete
     * @param sql
     * @param batchArgs
     * @return
     */
    public static int[] batchUpdate(String sql, List<Object[]> batchArgs) {
        return defaultExecutor.batchUpdate(sql,batchArgs);
    }
    /**
     * 分页查询
     * @param model
     * @param pageNumber 第几页
     * @param pageSize 每一页的大小
     * @param sql 查询语句
     * @param paras 查询参数
     * @return the Page object
     */
    public  static <T> Page<T> paginate(final Class<T> model,int pageNumber, int pageSize, String sql,Object[] paras) throws Exception {
        return  defaultExecutor.paginate(model,pageNumber,pageSize,sql,paras);
    }
}
