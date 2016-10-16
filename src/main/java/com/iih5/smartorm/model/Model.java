package com.iih5.smartorm.model;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.iih5.smartorm.dialect.DefaultDialect;
import com.iih5.smartorm.kit.StringKit;
import org.springframework.beans.BeanUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

public abstract class Model<M extends Model> implements Serializable {
    JdbcTemplate jdbc = null;
    private  StringBuffer limit = new StringBuffer();
    private  Long pageNumber;
    private  Integer pageSize;
    private  StringBuffer order = new StringBuffer();
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
     * 增加( + )  比如：incr("count",-100);
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
     * 乘（ * ）比如：mult("count",*100);
     * @param attr
     * @param value
     */
    public  M  mult(String attr, Object value){
        String number = String.valueOf(value);
        set(attr,attr+"*"+number);
        return (M)this;
    }
    /**
     * 除 （ / ） 比如：minus("count",/100);
     * @param attr
     * @param value
     */
    public  M  minus(String attr, Object value){
        String number = String.valueOf(value);
        set(attr,attr+"/"+number);
        return (M)this;
    }
    public Map<String, Object> getAttrs(){
        return attrs;
    }
    private void beanToAttrs() throws IntrospectionException, InvocationTargetException, IllegalAccessException {
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
    }
    /**
     * 添加保存到数据库
     *
     * @return 返回保存状态
     */
    public boolean insert() {
        try {
            beanToAttrs();
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
     * 保存并返回mysql自增长ID
     * @return
     */
    public Long insertAndReturnId(){
        if (insert()){
            String sql="SELECT LAST_INSERT_ID();";
            return  jdbc.queryForObject(sql,new Object[]{},Long.class);
        }
        return null;
    }
    /**
     * 根据条件删除数据
     *
     * @param conditions      操作条件，比如：conditions="userId=? and name=?"
     * @param conditionValues 参数比如：new Object[]{1000,'hill'};
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

    /**
     * 根据条件删除数据
     * @param cdtBean  把条件封装成一个bean对象
     * @return
     */
    public boolean delete(Object cdtBean) {
        String sql = "delete from "+table+" where 1=1 "+StringKit.beanToSqlConditionStr(cdtBean);
        if (jdbc.update(sql) < 0) {
            return false;
        }
        return true;
    }

    /**
     * 根据条件删除数据
     * @param conditions  操作条件，比如：conditions="userId=10000 and name='nick'"
     * @return
     */
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

    /**
     * 根据条件删除数据
     * @param id
     * @return
     */
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

    /**
     * 根据条件删除数据
     * @param list id列表
     * @return
     */
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

    /**
     * 根据条件删除数据
     * @param ids id列表
     * @return
     */
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
     * @param conditions       比如：conditions="userId=? and name=?"
     * @param conditionValues 比如：new Object[]{1000,'hill'};
     * @return true if delete succeed otherwise false
     */
    public boolean updateBy(String conditions, Object[] conditionValues) {
        try {
           beanToAttrs();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        if (getModifyFlag().isEmpty()) {
            return false;
        }
        StringBuilder sql = new StringBuilder();
        DefaultDialect.getDialect().forModelUpdate(table, " and "+conditions, attrs, getModifyFlag(), sql);
        if (jdbc.update(sql.toString(), conditionValues) < 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 根据条件更新
     * @param conditions
     * @return
     */
    public boolean updateBy(String conditions){
       return updateBy(conditions,new Object[]{});
    }

    /**
     * 根据条件更新
     * @param id 根据ID
     * @return
     */
    public boolean updateById(Object id){
        return updateBy("id=?",new Object[]{id});
    }

    /**
     * 根据条件更新
     * @param cdtBean 根据bean的对象
     * @return
     */
    public boolean updateBy(Object cdtBean) {
        try {
            beanToAttrs();
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
     * 替换，如果没有这直接插入，否则替换
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

    /**
     * 替换，如果没有这直接插入，否则替换
     * @param condition
     * @return
     */
    public boolean replaceBy(String condition){
        return replaceBy(condition,new Object[]{});
    }

    /**
     * 替换，如果没有这直接插入，否则替换
     * @param id
     * @return
     */
    public boolean replaceById(Object id){
        boolean rt = true;
        if (findById(id) == null){
            rt = insert();
        }else {
            rt = updateById(id);
        }
        return rt;
    }

    /**
     * 替换，如果没有这直接插入，否则替换
     * @param cdtBean
     * @return
     */
    public boolean replaceBy(Object cdtBean){
        boolean rt = true;
        if (findBy(cdtBean) == null){
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
        }if (result.size()==0){
            return null;
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

    /**
     * 根据ID查询对象
     * @param id
     * @return
     */
    public M findById(Object id){
      return findBy("id=?",new Object[]{id});
    }
    /**
     * 条件用bean对象表示
     * @param cdtBean
     * @return
     */
    public M findBy(Object cdtBean){
       List<M> list = findListBy(cdtBean);
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
    protected  <T> List<T> queryList(String columns, String conditions, Object[] conditionParas)  {
        String sql = DefaultDialect.getDialect().forModelFindBy(table, columns, conditions);
        if (order.length()>0){
            sql = sql+" "+order.toString();
        }
        if (limit.length()>0){
            sql = sql+" "+limit.toString();
        }
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
                            String column = fieldMap.get(field.getName());
                            if (column == null){
                                column = StringKit.toUnderscoreName(field.getName());
                                fieldMap.put(field.getName(),column);
                            }
                            if (columnMeta.contains(column)){
                                Object value = rs.getObject(column);
                                PropertyDescriptor pd = new PropertyDescriptor(field.getName(),mModel.getClass());
                                Method method = pd.getWriteMethod();
                                method.invoke(mModel,value);
                            }
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
     * 根据bean对象查询列表
     * @param cdtBean
     * @return
     */
    public List<M> findListBy(Object cdtBean){
        return queryList("*",StringKit.beanToSqlConditionStr(cdtBean),new Object[]{});
    }
    /**
     * 查找列表多少行
     * @param conditions
     * @param conditionParas
     * @return
     */
    public Long findListCountBy(String conditions, Object[] conditionParas){
        String sql = DefaultDialect.getDialect().forModelFindBy(table, " count(*) ", conditions);
        return  jdbc.queryForObject(sql,conditionParas, Long.class);
    }
    /**
     * 查找列表多少行
     * @param cdtBean
     * @return
     */
    public Long findListCountBy(Object cdtBean){
        String sql = "select count(*) from "+table+" where 1=1 "+StringKit.beanToSqlConditionStr(cdtBean);
        return jdbc.queryForObject(sql, NULL_PARA_ARRAY, Long.class);
    }

    /**
     *分页条件
     * @param pageNum 第几页
     * @param pageSize 每页多少行
     * @return
     */
    public M limit(Long pageNum,Integer pageSize){
        this.pageNumber = pageNum;
        if (pageNum<1){
            this.pageNumber = 1L;
        }
        this.pageSize = pageSize;
        Long start = (pageNum-1)*pageSize;
        limit.append(" limit ").append(start).append(", ").append(this.pageSize);
        return (M) this;
    }

    /**
     * 排序
     * @param column
     * @param sortType
     * @return
     */
    public M order(String column,String sortType){
        order.append(" order by ").append(column).append(" ").append(sortType);
        return (M) this;
    }

    /**
     * 分页查询
     * @param columns    字段名称，比如 columns="id,name,age"
     * @param conditions 查询条件，比如 conditions="user_id=? and age=?"
     * @param paras      查询参数
     * @return 返回对象列表
     * @
     */
    public Page<M> paginate(String columns, String conditions, Object[] paras)  {
        Long size = findListCountBy(conditions,paras);
        Long totalRow = size;
        if (totalRow == 0) {
            return new Page<M>(new ArrayList<M>(0), this.pageNumber, pageSize, 0L, 0L);
        }
        Long totalPage =  (totalRow / pageSize);
        if (totalRow % pageSize != 0) {
            totalPage++;
        }
        if (pageNumber > totalPage) {
            return new Page<M>(new ArrayList<M>(0), pageNumber, pageSize, totalPage, totalRow);
        }
        List<M> list =findListBy(columns, conditions, paras);
        return new Page<M>(list, pageNumber, pageSize, totalPage, totalRow);
    }

    /**
     * 分页查询
     * @param conditions
     * @param paras
     * @return
     */
    public Page<M> paginate( String conditions, Object[] paras)  {
        return  paginate("*",conditions,paras);
    }

    /**
     * 分页查询
     * @param ctdBean
     * @return
     */
    public Page<M> paginate(Object ctdBean)  {
        Long size = findListCountBy(ctdBean);
        Long totalRow = size;
        if (totalRow == 0) {
            return new Page<M>(new ArrayList<M>(0), this.pageNumber, pageSize, 0L, 0L);
        }
        Long totalPage =  (totalRow / pageSize);
        if (totalRow % pageSize != 0) {
            totalPage++;
        }
        if (pageNumber > totalPage) {
            return new Page<M>(new ArrayList<M>(0), pageNumber, pageSize, totalPage, totalRow);
        }
        List<M> list =findListBy(ctdBean);
        return new Page<M>(list, pageNumber, pageSize, totalPage, totalRow);
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
