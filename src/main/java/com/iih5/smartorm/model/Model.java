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

import com.alibaba.fastjson.JSON;
import com.iih5.smartorm.dialect.DefaultDialect;
import com.iih5.smartorm.kit.StringKit;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

public abstract class Model<M extends Model> implements Serializable {
    private static final long serialVersionUID = -990334519496260591L;
    JdbcTemplate jdbc = null;
    //表名
    private String table = "";
    private Map<String, Object> attrs = new HashMap<String, Object>();
    private Set<String> modifyFlag = new HashSet<String>();
    private Object[] NULL_PARA_ARRAY = new Object[]{};
    public Model() {
        this.table =  StringKit.toTableNameByModel(this.getClass());
        this.jdbc = getJdbc();
    }
    public Model(String table) {
        this.table = table;
        this.jdbc = getJdbc();
    }

    /**
     * 获取JDBC
     * @return
     */
    private JdbcTemplate getJdbc() {
        return  Db.getJdbcTemplate();
    }

    /**
     * 获取属性
     * @return
     */
    public Map<String, Object> getAttrs() {
        return attrs;
    }

    private Set<String> getModifyFlag() {
        return modifyFlag;
    }

    /**
     * 选择数据库源
     * @param dataSource
     * @return
     */
    public M use(String dataSource) {
        this.jdbc = Db.getJdbcTemplate(dataSource);
        return (M) this;
    }

    /**
     * Set attribute to model.
     *
     * @param attr  the attribute name of the model
     * @param value the value of the attribute
     * @return this model
     */
    public M set(String attr, Object value) {
        attrs.put(attr, value);
        getModifyFlag().add(attr);    // Add modify flag, update() need this flag.
        return (M) this;
    }

    /**
     *
     * @param attr
     * @param <T>
     * @return
     */
    public <T> T get(String attr) {
        return (T) (attrs.get(attr));
    }

    /**
     *
     * @param attr
     * @return
     */
    public String getStr(String attr) {
        return (String) attrs.get(attr);
    }

    /**
     *
     * @param attr
     * @return
     */
    public Integer getInt(String attr) {
        return (Integer) attrs.get(attr);
    }

    /**
     *
     * @param attr
     * @return
     */
    public Long getLong(String attr) {
        return (Long) attrs.get(attr);
    }

    /**
     *
     * @param attr
     * @return
     */
    public java.math.BigInteger getBigInteger(String attr) {
        return (java.math.BigInteger) attrs.get(attr);
    }

    /**
     *
     * @param attr
     * @return
     */
    public Date getDate(String attr) {
        return (Date) attrs.get(attr);
    }

    /**
     *
     * @param attr
     * @return
     */
    public java.sql.Time getTime(String attr) {
        return (java.sql.Time) attrs.get(attr);
    }

    /**
     *
     * @param attr
     * @return
     */
    public java.sql.Timestamp getTimestamp(String attr) {
        return (java.sql.Timestamp) attrs.get(attr);
    }

    /**
     *
     * @param attr
     * @return
     */
    public Double getDouble(String attr) {
        return (Double) attrs.get(attr);
    }

    /**
     *
     * @param attr
     * @return
     */
    public Float getFloat(String attr) {
        return (Float) attrs.get(attr);
    }

    /**
     *
     * @param attr
     * @return
     */
    public Boolean getBoolean(String attr) {
        return (Boolean) attrs.get(attr);
    }

    /**
     *
     * @param attr
     * @return
     */
    public java.math.BigDecimal getBigDecimal(String attr) {
        return (java.math.BigDecimal) attrs.get(attr);
    }

    /**
     *
     * @param attr
     * @return
     */
    public byte[] getBytes(String attr) {
        return (byte[]) attrs.get(attr);
    }

    /**
     *
     * @param attr
     * @return
     */
    public Number getNumber(String attr) {
        return (Number) attrs.get(attr);
    }

    /**
     * 添加保存到数据库
     *
     * @return 返回保存状态
     */
    public boolean save() {
        try {
            Field[] attr = this.getClass().getFields();
            for (Field f : attr) {
                Object value = f.get(this);
                if (value != null) {
                    attrs.put(f.getName(), value);
                }
            }
            StringBuilder sql = new StringBuilder();
            List<Object> paras = new ArrayList<Object>();
            DefaultDialect.getDialect().forModelSave(table, attrs, sql, paras);
            if (jdbc.update(sql.toString(), paras.toArray()) < 0) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    /**
     * 根据条件删除数据
     * @param conditions 比如：conditions="userId=? and name=?"
     * @param conditionValues 比如：new Object[]{1000,'hill'};
     * @return true if delete succeed otherwise false
     */
    public boolean delete(String conditions, Object[] conditionValues) {
        if (conditionValues==null || conditionValues.length==0){
            return false;
        }
        String sql = DefaultDialect.getDialect().deleteByCondition(table, conditions);
        if (jdbc.update(sql, conditionValues) < 0) {
            return false;
        }
        return true;
    }

    /**
     * 根据条件修改数据
     * @param conditions 比如：conditions="userId=? and name=?"
     * @param conditionValues 比如：new Object[]{1000,'hill'};
     * @return true if delete succeed otherwise false
     */
    public boolean update(String conditions, Object[] conditionValues) {
        try {
            Field[] attr = this.getClass().getFields();
            for (Field f : attr) {
                Object value = f.get(this);
                if (value != null) {
                    attrs.put(f.getName(), value);
                    modifyFlag.add(f.getName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        if (getModifyFlag().isEmpty()) {
            return false;
        }
        StringBuilder sql = new StringBuilder();
        DefaultDialect.getDialect().forModelUpdate(table, conditions, attrs, getModifyFlag(), sql);
        if (jdbc.update(sql.toString(), conditionValues) < 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 删除属性值
     * @param attr
     * @return this model
     */
    public M removeAttr(String attr) {
        attrs.remove(attr);
        getModifyFlag().remove(attr);
        return (M) this;
    }

    /**
     * 删除属性值
     * @param attrs
     * @return this model
     */
    public M removeAttr(String... attrs) {
        if (attrs != null)
            for (String a : attrs) {
                this.attrs.remove(a);
                this.getModifyFlag().remove(a);
            }
        return (M) this;
    }

    /**
     * 清空所有的属性值
     * @return
     */
    public M clear() {
        attrs.clear();
        getModifyFlag().clear();
        return (M) this;
    }
    /**
     *
     * @param columns 字段名称，比如 columns="id,name,age"
     * @param conditions conditions 查询条件，比如 conditions="user_id=? and age=?"
     * @param conditionParas 查询条件对应的参数
     * @return 返回Model对象
     * @throws Exception
     */
    public M find(String columns,String conditions,Object[] conditionParas)  throws Exception {
        List<M> result = findList(columns,conditions,conditionParas);
        return result.size() > 0 ? result.get(0) : null;
    }

    /**
     *
     * @param conditions conditions 查询条件，比如 conditions="user_id=? and age=?"
     * @param conditionParas 查询条件对应的参数
     * @return 返回Model对象
     * @throws Exception
     */
    public M find(String conditions,Object[] conditionParas)  throws Exception {
        List<M> result = findList(conditions,conditionParas);
        return result.size() > 0 ? result.get(0) : null;
    }

    /**
     *
     * @param conditions conditions 查询条件，比如 conditions="user_id=? and age=?"
     * @return 返回Model对象
     * @throws Exception
     */
    public M find(String conditions)  throws Exception {
        List<M> result = findList(conditions);
        return result.size() > 0 ? result.get(0) : null;
    }

    private Set<String> columnMeta= new HashSet<String>();
    /**
     * 查找Model对象列表
     * @param columns 字段名称，比如 columns="id,name,age"
     * @param conditions 查询条件，比如 conditions="user_id=? and age=?"
     * @param conditionParas 查询条件对应的参数
     * @param <T>
     * @return 返回Model对象列表
     * @throws Exception
     */
    <T> List<T> queryList(String columns,String conditions,Object[] conditionParas)  throws Exception {
        String sql=DefaultDialect.getDialect().forModelFindBy(table,columns,conditions);
        columnMeta.clear();
        return jdbc.query(sql, conditionParas, new RowMapper<T>() {
            public T mapRow(ResultSet rs, int rowNum) throws SQLException {
                try {
                    if (columnMeta.size()==0){
                        for (int i = 0; i <rs.getMetaData().getColumnCount() ; i++) {
                            String column= rs.getMetaData().getColumnName(i+1);
                            columnMeta.add(column);
                        }
                    }
                    Model<?> mModel = getUsefulClass().newInstance();
                    Field[] fields = mModel.getClass().getFields();
                    if (fields.length > 0) {
                        for (Field f : fields) {
                            if (columnMeta.contains(f.getName())){
                                f.set(mModel,rs.getObject(f.getName()));
                            }
                        }
                    }else {
                        ResultSetMetaData rsmd = rs.getMetaData();
                        int columnCount = rsmd.getColumnCount();
                        Map<String, Object> attrs = mModel.getAttrs();
                        for (int i = 1; i <= columnCount; i++) {
                            Object value = rs.getObject(i);
                            if (value!=null){
                                attrs.put(rsmd.getColumnName(i), value);
                            }
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
     * @param columns 字段名称，比如 columns="id,name,age"
     * @param conditions 查询条件，比如 conditions="user_id=? and age=?"
     * @param conditionParas 查询条件对应的参数
     * @return 返回Model对象列表
     * @throws Exception
     */
    public  List<M> findList(String columns,String conditions,Object[] conditionParas)  throws Exception {
       return queryList(columns, conditions, conditionParas);
    }
    /**
     * 查找Model对象列表
     * @param conditions  查询条件，比如 conditions="user_id=? and age=?"
     * @param conditionParas 查询条件对应的参数
     * @return 返回Model对象列表
     * @throws Exception
     */
    public List<M> findList(String conditions,Object[] conditionParas)  throws Exception {
        return findList("*",conditions,conditionParas);
    }

    /**
     *
     * @param conditions 查询条件，比如 conditions="user_id=? and age=?"
     * @return 返回Model对象列表
     * @throws Exception
     */
    public List<M> findList(String conditions)  throws Exception {
        return findList(conditions,NULL_PARA_ARRAY);
    }
    /**
     * 查找基础对象列表(Boolean,Int,Number,Float,String...)
     * @param column 字段名称，比如 columns="id,name,age"
     * @param conditions 查询条件，比如 conditions="user_id=? and age=?"
     * @param conditionParas 查询条件对应的参数
     * @param classType  (Boolean,Int,Number,Float,String...)
     * @param <T>
     * @return 返回基础对象列表
     * @throws Exception
     */
    public  <T> List<T> findBasicObjectList(String column,String conditions,Object[] conditionParas, Class<T> classType) throws Exception {
        String sql=DefaultDialect.getDialect().forModelFindBy(table,column,conditions);
        return  jdbc.queryForList(sql,conditionParas,classType);
    }

    /**
     * 查找基础对象列表(Boolean,Int,Number,Float,String...)
     * @param column 字段名称，比如 columns="id,name,age"
     * @param conditions 查询条件，比如 conditions="user_id=? and age=?"
     * @param classType  (Boolean,Int,Number,Float,String...)
     * @return 返回基础对象列表
     * @throws Exception
     */
    public  <T> List<T> findBasicObjectList(String column,String conditions,  Class<T> classType) throws Exception {
        return findBasicObjectList(column,conditions,NULL_PARA_ARRAY,classType);
    }


    /**
     * 查找基础对象(Boolean,Int,Number,Float,String...)
     * @param column 字段名称，比如 columns="id,name,age"
     * @param conditions 查询条件，比如 conditions="user_id=? and age=?"
     * @param conditionParas 查询条件对应的参数
     * @param classType (Boolean,Int,Number,Float,String...)
     * @return 返回基础对象
     */
    public <T> T findBasicObject(String column,String conditions,Object[] conditionParas, Class<T> classType) throws Exception {
        String sql=DefaultDialect.getDialect().forModelFindBy(table,column,conditions);
        return  jdbc.queryForObject(sql,conditionParas,classType);
    }

    /**
     * 查找基础对象 (Boolean,Int,Number,Float,String...)
     * @param column 字段名称，比如 columns="id,name,age"
     * @param conditions 查询条件，比如 conditions="user_id=? and age=?"
     * @param classType (Boolean,Int,Number,Float,String...)
     * @param <T>
     * @return 返回基础对象
     */
    public  <T> T findBasicObject(String column,String conditions, Class<T> classType) throws Exception {
        return (T) findBasicObject(column,conditions,NULL_PARA_ARRAY,classType);
    }

    /**
     * 分页查询
     * @param pageNumber 第几页
     * @param pageSize 每一页的大小
     * @param columns 字段名称，比如 columns="id,name,age"
     * @param conditions 查询条件，比如 conditions="user_id=? and age=?"
     * @param paras 查询参数
     * @return 返回对象列表
     * @throws Exception
     */
    public   Page<M> paginate(int pageNumber, int pageSize,String columns ,String conditions,Object[] paras) throws Exception {
        String sql=DefaultDialect.getDialect().forModelFindBy(table,columns,conditions);
        return (Page<M>)Db.paginate(this.getUsefulClass(),pageNumber,pageSize,sql,paras);
    }

    /**
     * 分页查询
     * @param pageNumber 第几页
     * @param pageSize 每一页的大小
     * @param columns 字段名称，比如 columns="id,name,age"
     * @param conditions 查询条件，比如 conditions="user_id=? and age=?"
     * @param paras 查询参数
     * @param sortColumn 排序字段
     * @param sortType 排序类型（升或降）
     * @return 返回对象列表
     * @throws Exception
     */
    public   Page<M> paginateOrderBy(int pageNumber, int pageSize,String columns ,String conditions,Object[] paras,String sortColumn,String sortType) throws Exception {
        String sql=DefaultDialect.getDialect().forModelFindBy(table,columns,conditions)+" order by "+sortColumn+" "+sortType+" ";
        return (Page<M>)Db.paginate(this.getUsefulClass(),pageNumber,pageSize,sql,paras);
    }

    /**
     *
     * @param o
     * @return
     */
    public boolean equals(Object o) {
        if (!(o instanceof Model))
            return false;
        if (getUsefulClass() != ((Model) o).getUsefulClass())
            return false;
        if (o == this)
            return true;
        return this.attrs.equals(((Model) o).attrs);
    }

    /**
     *
     * @return
     */
    public int hashCode() {
        return (attrs == null ? 0 : attrs.hashCode()) ^ (getModifyFlag() == null ? 0 : getModifyFlag().hashCode());
    }

    /**
     * 转换为json字符串
     * @return json str
     */
    public String toString() {
        if (this.getClass().getFields().length > 0) {
            return JSON.toJSONString(this);
        }
        Map<String, Object> ddd = this.getAttrs();
        return JSON.toJSONString(this.getAttrs());
    }

    /**
     *
     * @return
     */
    private Class<? extends Model> getUsefulClass() {
        Class c = getClass();
        return c.getName().indexOf("EnhancerByCGLIB") == -1 ? c : c.getSuperclass();
    }
}
