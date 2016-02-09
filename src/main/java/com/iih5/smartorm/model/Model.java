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
    private JdbcTemplate getJdbc() {
        return  Db.getJdbcTemplate();
    }
    public Map<String, Object> getAttrs() {
        return attrs;
    }

    private Set<String> getModifyFlag() {
        return modifyFlag;
    }

    public M use(String db) {
        this.jdbc = Db.getJdbcTemplate(db);
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

    public <T> T get(String attr) {
        return (T) (attrs.get(attr));
    }

    public String getStr(String attr) {
        return (String) attrs.get(attr);
    }

    public Integer getInt(String attr) {
        return (Integer) attrs.get(attr);
    }
    public Long getLong(String attr) {
        return (Long) attrs.get(attr);
    }

    public java.math.BigInteger getBigInteger(String attr) {
        return (java.math.BigInteger) attrs.get(attr);
    }

    public Date getDate(String attr) {
        return (Date) attrs.get(attr);
    }

    public java.sql.Time getTime(String attr) {
        return (java.sql.Time) attrs.get(attr);
    }

    public java.sql.Timestamp getTimestamp(String attr) {
        return (java.sql.Timestamp) attrs.get(attr);
    }

    public Double getDouble(String attr) {
        return (Double) attrs.get(attr);
    }

    public Float getFloat(String attr) {
        return (Float) attrs.get(attr);
    }

    public Boolean getBoolean(String attr) {
        return (Boolean) attrs.get(attr);
    }

    public java.math.BigDecimal getBigDecimal(String attr) {
        return (java.math.BigDecimal) attrs.get(attr);
    }

    public byte[] getBytes(String attr) {
        return (byte[]) attrs.get(attr);
    }

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
     * 查找Model对象列表
     * @param sql
     * @param paras
     * @return model list
     * @throws Exception
     */
    public <T> List<T> findList(String sql, Object[] paras) throws Exception {
        return jdbc.query(sql, paras, new RowMapper<T>() {
            public T mapRow(ResultSet rs, int rowNum) throws SQLException {
                try {
                    Model<?> mModel = getUsefulClass().newInstance();
                    Field[] fields = mModel.getClass().getFields();
                    if (fields.length > 0) {
                        for (Field f : fields) {
                            f.set(mModel, rs.getObject(f.getName()));
                        }
                    } else {
                        ResultSetMetaData rsmd = rs.getMetaData();
                        int columnCount = rsmd.getColumnCount();
                        Map<String, Object> attrs = mModel.getAttrs();
                        for (int i = 1; i <= columnCount; i++) {
                            Object value = rs.getObject(i);
                            attrs.put(rsmd.getColumnName(i), value);
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
     * @return model list
     * @throws Exception
     */
    public List<M> findList(String sql) throws Exception {
        return findList(sql, NULL_PARA_ARRAY);
    }
    /**
     * 查找Model对象
     * @param sql
     * @return this model
     * @throws Exception
     */
    public M find(String sql, Object[] paras) throws Exception {
        List<M> result = findList(sql, paras);
        return result.size() > 0 ? result.get(0) : null;
    }
    /**
     * 查找Model对象
     * @param sql
     * @return this model
     * @throws Exception
     */
    public M find(String sql) throws Exception {
        return find(sql,NULL_PARA_ARRAY);
    }

    /**
     * 查找基础对象
     * @param sql
     * @param paras
     * @param classType (Boolean,Int,Number,Float,String...)
     * @param <T>
     * @return
     */
    public static <T> T findBasicObject(String sql, Object[] paras, Class<T> classType) {
        return Db.findBasicObject(sql,paras,classType);
    }

    /**
     * 查找基础对象
     * @param sql
     * @param classType (Boolean,Int,Number,Float,String...)
     * @param <T>
     * @return
     */
    public  <T> T findBasicObject(String sql, Class<T> classType) {
        return Db.findBasicObject(sql,classType);
    }

    /**
     * 查找基础对象列表
     * @param sql
     * @param paras
     * @param classType (Boolean,Int,Number,Float,String...)
     * @param <T>
     * @return
     */
    public  <T> List<T> findBasicObjectList(String sql, Object[] paras, Class<T> classType) {
        return Db.findBasicObjectList(sql,paras,classType);
    }

    /**
     * 查找基础对象列表
     * @param sql
     * @param classType (Boolean,Int,Number,Float,String...)
     * @param <T>
     * @return
     */
    public  <T> List<T> findBasicObjectList(String sql, Class<T> classType) {
        return Db.findBasicObjectList(sql,classType);
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
        return Db.paginate(model,pageNumber,pageSize,sql,paras);
    }



    public boolean equals(Object o) {
        if (!(o instanceof Model))
            return false;
        if (getUsefulClass() != ((Model) o).getUsefulClass())
            return false;
        if (o == this)
            return true;
        return this.attrs.equals(((Model) o).attrs);
    }

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

    private Class<? extends Model> getUsefulClass() {
        Class c = getClass();
        return c.getName().indexOf("EnhancerByCGLIB") == -1 ? c : c.getSuperclass();
    }
}
