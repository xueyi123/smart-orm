package com.iih5.smartorm.model;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.PropertyFilter;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
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
    protected String table;//表名
    @JSONField(serialize = false)
    private Map<String, Object> attrs = new HashMap<String, Object>();
    private Set<String> modifyFlag = new HashSet<String>();
    private Object[] NULL_PARA_ARRAY = new Object[]{};
    public Model() {
        this.table = StringKit.toTableNameByModel(this.getClass());
        this.jdbc = getJdbc();
    }
    public Model(Object copyBean) {
        BeanUtils.copyProperties(copyBean,this);
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
     * 增加( + )
     * @param attr
     * @param value
     */
    public  M  incr(String attr, Object value){
        String number = String.valueOf(value);
        if (number.substring(0,1).equals("-")){
            set(attr,attr+number);
        }else {
            set(attr,attr+"+"+number);
        }
        return (M)this;
    }
    /**
     * 乘（ * ）
     * @param attr
     * @param value
     */
    public  M  mult(String attr, Object value){
        String number = String.valueOf(value);
        set(attr,attr+"*"+number);
        return (M)this;
    }
    /**
     * 除 （ / ）
     * @param attr
     * @param value
     */
    public  M  minus(String attr, Object value){
        String number = String.valueOf(value);
        set(attr,attr+"/"+number);
        return (M)this;
    }
    /**
     * @param attr
     * @param <T>
     * @return
     */
    public <T> T get(String attr) {
        return (T) (attrs.get(attr));
    }
    public Map<String, Object> getAttrs(){
        return attrs;
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
            return  Db.findBasicObject(sql,new Object[]{},Long.class);
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
        String sql = "delete from "+table+" where 1=1 "+StringKit.beanToSqlConditionStr(cdtBean);
        if (jdbc.update(sql) < 0) {
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
    public boolean updateBy(String conditions, Object[] conditionValues) {
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
    public boolean updateBy(String conditions){
       return updateBy(conditions,new Object[]{});
    }
    public boolean updateById(Object id){
        return updateBy("id=?",new Object[]{id});
    }
    public boolean updateBy(Object cdtBean) {
        try {
            Field[] fields = this.getClass().getDeclaredFields();
            for (Field field : fields) {
                PropertyDescriptor pd = new PropertyDescriptor(field.getName(),this.getClass());
                Method methodReader = pd.getReadMethod();
                Object value= methodReader.invoke(this);
                if (value != null) {
                    attrs.put(StringKit.toUnderscoreName(field.getName()), value);
                    modifyFlag.add(StringKit.toUnderscoreName(field.getName()));
                }
            }
            if (getModifyFlag().isEmpty()) {
                return false;
            }
            StringBuilder sql = new StringBuilder();
            DefaultDialect.getDialect().forModelUpdate(table, StringKit.beanToSqlConditionStr(cdtBean), attrs, getModifyFlag(), sql);
            if (jdbc.update(sql.toString()) < 0) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 替换
     * @param condition
     * @param paras
     * @return
     */
    public boolean replaceBy(String condition, Object[] paras){
        boolean rt = true;
        if (findBy(condition,paras) == null){
            rt = insert();
        }else {
            rt = updateBy(condition, paras);
        }
        return rt;
    }
    public boolean replaceBy(String condition){
        return replaceBy(condition,new Object[]{});
    }
    public boolean replaceById(Object id){
        boolean rt = true;
        if (findById(id) == null){
            rt = insert();
        }else {
            rt = updateById(id);
        }
        return rt;
    }
    public boolean replaceBy(Object cdtBean){
        boolean rt = true;
        if (findBy(StringKit.beanToSqlConditionStr(cdtBean)) == null){
            rt = insert();
        }else {
            rt =  updateBy(cdtBean);
        }
        return rt;
    }


    /**
     * @param columns        字段名称，比如 columns="id,name,age"
     * @param conditions     conditions 查询条件，比如 conditions="user_id=? and age=?"
     * @param conditionParas 查询条件对应的参数
     * @return 返回Model对象
     * @
     */
    public M findBy(String columns, String conditions, Object[] conditionParas)  {
        List<M> result = findListBy(columns, conditions, conditionParas);
        if (result.size()>1){
            throw new DataException("返回多于1条数据");
        }
        return  result.get(0);
    }
    /**
     * @param conditions     conditions 查询条件，比如 conditions="user_id=? and age=?"
     * @param conditionParas 查询条件对应的参数
     * @return 返回Model对象 1
     * @
     */
    public M findBy(String conditions, Object[] conditionParas)  {
        return findBy("*",conditions,conditionParas);
    }
    /**
     * @param conditions conditions 查询条件，比如 conditions="user_id=? and age=?"
     * @return 返回Model对象
     * @
     */
    public M findBy(String conditions)  {
        return findBy(conditions,new Object[]{});
    }
    public M findById(Object id){
      return findBy("id=?",new Object[]{id});
    }
    /**
     * 条件用bean对象表示
     * @param cdtBean
     * @return
     */
    public M findBy(Object cdtBean){
       List<M> list = findList(cdtBean);
        if (list.size()>1){
            throw new DataException("返回多于1条数据");
        }else if (list.size()==0){
            return null;
        }
        return list.get(0);
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
                    if (columnMeta.size() <= 0) {
                        for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                            String column = rs.getMetaData().getColumnLabel(i + 1);
                            columnMeta.add(column);
                        }
                    }
                    Model<?> mModel = getUsefulClass().newInstance();
                    Field[] fields = mModel.getClass().getDeclaredFields();
                    if (fields.length > 0) {
                        for (Field field:fields) {
                            Object value = rs.getObject(field.getName());
                            String property = fieldMap.get(field.getName());
                            if (property == null){
                                property = StringKit.toCamelCaseName(field.getName());
                                fieldMap.put(field.getName(),property);
                            }
                            PropertyDescriptor pd = new PropertyDescriptor(property,mModel.getClass());
                            Method method = pd.getWriteMethod();
                            method.invoke(mModel,value);
                        }
                    } else {
                        ResultSetMetaData rsmd = rs.getMetaData();
                        int columnCount = rsmd.getColumnCount();
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
    public List<M> findListBy(String columns, String conditions, Object[] conditionParas)  {
        return queryList(columns, " and "+conditions, conditionParas);
    }
    /**
     * 查找Model对象列表
     *
     * @param conditions     查询条件，比如 conditions="user_id=? and age=?"
     * @param conditionParas 查询条件对应的参数
     * @return 返回Model对象列表
     * @
     */
    public List<M> findListBy(String conditions, Object[] conditionParas)  {
        return findListBy("*", conditions, conditionParas);
    }
    /**
     * @param conditions 查询条件，比如 conditions="user_id=? and age=?"
     * @return 返回Model对象列表
     * @
     */
    public List<M> findListBy(String conditions)  {
        return findListBy(conditions, NULL_PARA_ARRAY);
    }
    /**
     * 查找基础对象列表(Boolean,Int,Number,Float,String...)
     *
     * @param columns         字段名称，比如 columns="id,name,age"
     * @param conditions     查询条件，比如 conditions="user_id=? and age=?"
     * @param conditionParas 查询条件对应的参数
     * @param claszz      (Boolean,Int,Number,Float,String...)
     * @param <T>
     * @return 返回基础对象列表
     * @
     */
    public <T> List<T> findListBy(String columns, String conditions, Object[] conditionParas, Class<T> claszz)  {
        String sql = DefaultDialect.getDialect().forModelFindBy(table, columns, conditions);
        return jdbc.queryForList(sql, conditionParas, claszz);
    }
    /**
     * 查找基础对象列表(Boolean,Int,Number,Float,String...)
     *
     * @param columns     字段名称，比如 columns="id,name,age"
     * @param conditions 查询条件，比如 conditions="user_id=? and age=?"
     * @param claszz  (Boolean,Int,Number,Float,String...)
     * @return 返回基础对象列表
     * @
     */
    public <T> List<T> findObjectList(String columns, String conditions, Class<T> claszz)  {
        return findListBy(columns, conditions, NULL_PARA_ARRAY, claszz);
    }
    public List<M> findList(Object cdtBean){
        return queryList("*",StringKit.beanToSqlConditionStr(cdtBean),new Object[]{});
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
        return JSON.toJSONString(attrs);
    }
    /**
     * @return
     */
    private Class<? extends Model> getUsefulClass() {
        Class c = getClass();
        return c.getName().indexOf("EnhancerByCGLIB") == -1 ? c : c.getSuperclass();
    }
}
