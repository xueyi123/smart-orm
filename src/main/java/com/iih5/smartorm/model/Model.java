package com.iih5.smartorm.model;

import com.alibaba.fastjson.JSON;
import com.iih5.smartorm.dialect.DefaultDialect;
import com.iih5.smartorm.kit.StringKit;
import org.springframework.beans.BeanUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

public abstract class Model<M extends Model> implements Serializable {
    private static final long serialVersionUID = -990334519496260591L;
    JdbcTemplate jdbc = null;
    protected String table = "";//表名
    private Map<String, Object> attrs = new HashMap<String, Object>();
    private Set<String> modifyFlag = new HashSet<String>();
    private Object[] NULL_PARA_ARRAY = new Object[]{};

    public Model() {
        this.table = StringKit.toTableNameByModel(this.getClass());
        this.jdbc = getJdbc();
    }

    public Model(Object srcBean) {
        BeanUtils.copyProperties(srcBean,this);
        this.jdbc = getJdbc();
    }

    /**
     * 获取JDBC
     *
     * @return
     */
    private JdbcTemplate getJdbc() {
        return Db.getJdbcTemplate();
    }
    private Set<String> getModifyFlag() {
        return modifyFlag;
    }
    /**
     * 选择数据库源
     *
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
     * @param attr
     * @param <T>
     * @return
     */
    public <T> T get(String attr) {
        return (T) (attrs.get(attr));
    }

    /**
     * 添加保存到数据库
     *
     * @return 返回保存状态
     */
    public boolean insert() {
        try {
            Field[] fields = this.getClass().getDeclaredFields();
            for (Field field : fields) {
                PropertyDescriptor pd = new PropertyDescriptor(field.getName(),this.getClass());
                Method methodReader = pd.getReadMethod();
                Object value= methodReader.invoke(this);
                if (value != null) {
                    attrs.put(StringKit.toUnderscoreName(field.getName()), value);
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
     * 保存并返回自增长ID
     * @return
     */
    public Long insertAndReturnId(){
        if (insert()){
            String sql="SELECT LAST_INSERT_ID();";
            return  Db.query(sql,new Object[]{},Long.class);
        }
        return null;
    }
    /**
     * 根据条件删除数据
     *
     * @param conditions      比如：conditions="userId=? and name=?"
     * @param conditionValues 比如：new Object[]{1000,'hill'};
     * @return true if delete succeed otherwise false
     */
    public boolean delete(String conditions, Object[] conditionValues) {
        if (conditionValues == null || conditionValues.length == 0) {
            return false;
        }
        String sql = DefaultDialect.getDialect().deleteByCondition(table, conditions);
        if (jdbc.update(sql, conditionValues) < 0) {
            return false;
        }
        return true;
    }
    public boolean delete(Object cdtBean) {
        if (conditionValues == null || conditionValues.length == 0) {
            return false;
        }
        String sql = DefaultDialect.getDialect().deleteByCondition(table, conditions);
        if (jdbc.update(sql, conditionValues) < 0) {
            return false;
        }
        return true;
    }
    public boolean delete(String conditions) {
        StringBuilder sql = new StringBuilder();
        sql.append("delete from ");
        sql.append(table);
        sql.append(" where ");
        sql.append(conditions);
        if (jdbc.update(sql.toString()) < 0) {
            return false;
        }
        return true;
    }
    public boolean deleteById(Object id) {
        StringBuilder sql = new StringBuilder();
        sql.append("delete from ");
        sql.append(table);
        sql.append(" where id= ");
        sql.append(id);
        if (jdbc.update(sql.toString()) < 0) {
            return false;
        }
        return true;
    }
    public boolean deleteByIds(List list) {
        String st1=list.toString();
        String arr = st1.substring(st1.indexOf("[")+1,st1.indexOf("]"));
        StringBuilder sql = new StringBuilder();
        sql.append("delete from ");
        sql.append(table);
        sql.append(" where id in ");
        sql.append("(");
        sql.append(arr);
        sql.append(")");
        if (jdbc.update(sql.toString()) < 0) {
            return false;
        }
        return true;
    }
    public boolean deleteByIds(Object... ids) {
        String str = JSON.toJSONString(ids) ;
        return deleteByIds(str);
    }
    private boolean deleteByIds(String str) {
        String arr = str.substring(str.indexOf("[") + 1, str.indexOf("]"));
        StringBuilder sql = new StringBuilder();
        sql.append("delete from ");
        sql.append(table);
        sql.append(" where id in ");
        sql.append("(");
        sql.append(arr);
        sql.append(")");
        if (jdbc.update(sql.toString()) < 0) {
            return false;
        }
        return true;
    }



    /**
     * 根据条件修改数据
     *
     * @param conditions      比如：conditions="userId=? and name=?"
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
    public boolean update(String cdtBean) {
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

    public boolean updateById(Long id){
        return update("id=?",new Object[]{id});
    }
    /**
     * 替换
     * @param id
     * @return
     */
    public  boolean  replaceById(Long id){
        boolean rt = true;
        if (findById(id) == null){
            rt = save();
        }else {
            rt = updateById(id);
        }
        return rt;
    }
    /**
     * 替换
     * @param condition
     * @param paras
     * @return
     */
    public  boolean  replaceBy(String condition, Object[] paras){
        boolean rt = true;
        if (findBy(condition,paras) == null){
            rt = save();
        }else {
            rt = updateBy(condition, paras);
        }
        return rt;
    }

    /**
     * 清空所有的属性值
     * @return
     */


    /**
     * @param columns        字段名称，比如 columns="id,name,age"
     * @param conditions     conditions 查询条件，比如 conditions="user_id=? and age=?"
     * @param conditionParas 查询条件对应的参数
     * @return 返回Model对象
     * @
     */
    public M find(String columns, String conditions, Object[] conditionParas)  {
        List<M> result = findList(columns, conditions, conditionParas);
        return result.size() > 0 ? result.get(0) : null;
    }
    /**
     * @param conditions     conditions 查询条件，比如 conditions="user_id=? and age=?"
     * @param conditionParas 查询条件对应的参数
     * @return 返回Model对象 1
     * @
     */
    public M find(String conditions, Object[] conditionParas)  {
        List<M> result = findList(conditions, conditionParas);
        return result.size() > 0 ? result.get(0) : null;
    }
    /**
     * @param conditions conditions 查询条件，比如 conditions="user_id=? and age=?"
     * @return 返回Model对象
     * @
     */
    public M find(String conditions)  {
        List<M> result = findList(conditions);
        return result.size() > 0 ? result.get(0) : null;
    }
    public M findById(long id){
      return find("id="+id);
    }
    /**
     * 查找Model对象列表
     *
     * @param columns        字段名称，比如 columns="id,name,age"
     * @param conditions     查询条件，比如 conditions="user_id=? and age=?"
     * @param conditionParas 查询条件对应的参数
     * @param <T>
     * @return 返回Model对象列表
     * @
     */
    <T> List<T> queryList(String columns, String conditions, Object[] conditionParas)  {
        String sql = DefaultDialect.getDialect().forModelFindBy(table, columns, conditions);
        final Set<String> columnMeta = new HashSet<String>();
        final Map<String,String> fieldMap = new HashMap<String, String>();
        return jdbc.query(sql, conditionParas, new RowMapper<T>() {
            public T mapRow(ResultSet rs, int rowNum) throws SQLException {
                try {
                    if (columnMeta.size() == 0) {
                        for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                            String column = rs.getMetaData().getColumnLabel(i + 1);
                            columnMeta.add(column);
                        }
                    }
                    Model<?> mModel = getUsefulClass().newInstance();
                    Field[] fields = mModel.getClass().getFields();
                    if (fields.length > 0) {
//                        for (Field f : fields) {
//                            if (columnMeta.contains(f.getName())) {
//                                f.set(mModel, rs.getObject(f.getName()));
//                            }
//                        }
                        //------
                        for (Field fd:fields) {
                            String column = fieldMap.get(fd.getName());
                            if (column == null){
                               String tmpN = StringKit.toUnderscoreName(fd.getName());
                               fieldMap.put(column,tmpN);
                            }
                            Object value = rs.getObject(column);
                            PropertyDescriptor pd = new PropertyDescriptor(fd.getName(),mModel.getClass());
                            Method method = pd.getWriteMethod();
                            method.invoke(mModel,value);
                        }
                        //------
                    } else {
                        ResultSetMetaData rsmd = rs.getMetaData();
                        int columnCount = rsmd.getColumnCount();
                        Map<String, Object> attrs = mModel.getAttrs();
                        for (int i = 1; i <= columnCount; i++) {
                            Object value = rs.getObject(i);
                            if (value != null) {
                                attrs.put(rsmd.getColumnLabel(i), value);
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
     *
     * @param columns        字段名称，比如 columns="id,name,age"
     * @param conditions     查询条件，比如 conditions="user_id=? and age=?"
     * @param conditionParas 查询条件对应的参数
     * @return 返回Model对象列表
     * @
     */
    public List<M> findList(String columns, String conditions, Object[] conditionParas)  {
        return queryList(columns, conditions, conditionParas);
    }
    /**
     * 查找Model对象列表
     *
     * @param conditions     查询条件，比如 conditions="user_id=? and age=?"
     * @param conditionParas 查询条件对应的参数
     * @return 返回Model对象列表
     * @
     */
    public List<M> findList(String conditions, Object[] conditionParas)  {
        return findList("*", conditions, conditionParas);
    }
    /**
     * @param conditions 查询条件，比如 conditions="user_id=? and age=?"
     * @return 返回Model对象列表
     * @
     */
    public List<M> findList(String conditions)  {
        return findList(conditions, NULL_PARA_ARRAY);
    }
    /**
     * 获取Map格式列表(不包含attrs包裹属性)
     *
     * @param sql
     * @return
     */
    public List<Map<String, Object>> findList(String sql,boolean isNotAttr) {
        return jdbc.queryForList(sql);
    }
    /**
     * 获取Map格式列表(不包含attrs包裹属性)
     *
     * @param sql
     * @param paras
     * @return
     */
    public List<Map<String, Object>> findList(String sql, Object[] paras,boolean isNotAttr) {
        return jdbc.queryForList(sql, paras);
    }
    /**
     * 查找基础对象列表(Boolean,Int,Number,Float,String...)
     *
     * @param column         字段名称，比如 columns="id,name,age"
     * @param conditions     查询条件，比如 conditions="user_id=? and age=?"
     * @param conditionParas 查询条件对应的参数
     * @param classType      (Boolean,Int,Number,Float,String...)
     * @param <T>
     * @return 返回基础对象列表
     * @
     */
    public <T> List<T> findBasicObjectList(String column, String conditions, Object[] conditionParas, Class<T> classType)  {
        String sql = DefaultDialect.getDialect().forModelFindBy(table, column, conditions);
        return jdbc.queryForList(sql, conditionParas, classType);
    }
    /**
     * 查找基础对象列表(Boolean,Int,Number,Float,String...)
     *
     * @param column     字段名称，比如 columns="id,name,age"
     * @param conditions 查询条件，比如 conditions="user_id=? and age=?"
     * @param classType  (Boolean,Int,Number,Float,String...)
     * @return 返回基础对象列表
     * @
     */
    public <T> List<T> findBasicObjectList(String column, String conditions, Class<T> classType)  {
        return findBasicObjectList(column, conditions, NULL_PARA_ARRAY, classType);
    }
    /**
     * 查找基础对象(Boolean,Int,Number,Float,String...)
     *
     * @param column         字段名称，比如 columns="id,name,age"
     * @param conditions     查询条件，比如 conditions="user_id=? and age=?"
     * @param conditionParas 查询条件对应的参数
     * @param classType      (Boolean,Int,Number,Float,String...)
     * @return 返回基础对象
     */
    public <T> T findBasicObject(String column, String conditions, Object[] conditionParas, Class<T> classType)  {
        String sql = DefaultDialect.getDialect().forModelFindBy(table, column, conditions);
        return jdbc.queryForObject(sql, conditionParas, classType);
    }
    /**
     * 查找基础对象 (Boolean,Int,Number,Float,String...)
     *
     * @param column     字段名称，比如 columns="id,name,age"
     * @param conditions 查询条件，比如 conditions="user_id=? and age=?"
     * @param classType  (Boolean,Int,Number,Float,String...)
     * @param <T>
     * @return 返回基础对象
     */
    public <T> T findBasicObject(String column, String conditions, Class<T> classType)  {
        return (T) findBasicObject(column, conditions, NULL_PARA_ARRAY, classType);
    }
    /**
     * 分页查询
     *
     * @param pageNumber 第几页
     * @param pageSize   每一页的大小
     * @param columns    字段名称，比如 columns="id,name,age"
     * @param conditions 查询条件，比如 conditions="user_id=? and age=?"
     * @param paras      查询参数
     * @return 返回对象列表
     * @
     */
    public Page<M> paginate(int pageNumber, int pageSize, String columns, String conditions, Object[] paras)  {
        String sql = DefaultDialect.getDialect().forModelFindBy(table, columns, conditions);
        try {
            return (Page<M>) Db.paginate(this.getUsefulClass(), pageNumber, pageSize, sql, paras);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 分页查询
     *
     * @param pageNumber 第几页
     * @param pageSize   每一页的大小
     * @param columns    字段名称，比如 columns="id,name,age"
     * @param conditions 查询条件，比如 conditions="user_id=? and age=?"
     * @param paras      查询参数
     * @param isMap      是否返回MAP格式 true 返回MAP格式(不包含attrs包裹属性)
     * @return 返回对象列表
     * @
     */
    public Page<Map> paginate(int pageNumber, int pageSize, String columns, String conditions, Object[] paras,boolean isMap)  {
        String sql = DefaultDialect.getDialect().forModelFindBy(table, columns, conditions);
        try {
            return (Page<Map>) Db.paginate(this.getUsefulClass(), pageNumber, pageSize, sql, paras,isMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
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
     * @return
     */
    public int hashCode() {
        return (attrs == null ? 0 : attrs.hashCode()) ^ (getModifyFlag() == null ? 0 : getModifyFlag().hashCode());
    }
    /**
     * 转换为json字符串
     *
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
     * @return
     */
    private Class<? extends Model> getUsefulClass() {
        Class c = getClass();
        return c.getName().indexOf("EnhancerByCGLIB") == -1 ? c : c.getSuperclass();
    }
}
