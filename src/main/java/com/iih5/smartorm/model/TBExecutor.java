package com.iih5.smartorm.model;

import com.iih5.smartorm.dialect.DefaultDialect;
import com.iih5.smartorm.kit.SqlXmlKit;
import com.iih5.smartorm.kit.StringKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

public  abstract class TBExecutor<M extends TBExecutor> implements Serializable {
    static Logger logger = LoggerFactory.getLogger(TBExecutor.class);
    JdbcTemplate jdbc = null;
    private  StringBuffer limit = new StringBuffer();
    private  Long pageNumber;
    private  Integer pageSize;
    private  StringBuffer order = new StringBuffer();
    protected String table;//数据表名
    protected Model model;//数据模型
    private transient  Map<String, Object> attrs = new HashMap<String, Object>();
    private Set<String> modifyFlag = new HashSet<String>();
    private Object[] NULL_PARA_ARRAY = new Object[]{};
    public TBExecutor() {
        this.jdbc = getJdbc();
    }
    public TBExecutor(Object copyBean) {
        BeanUtils.copyProperties(copyBean,this);
        this.jdbc = getJdbc();
    }
    /**
     * 获取JDBC
     *
     * @return
     */
    private JdbcTemplate getJdbc() {
        return DB.getJdbcTemplate();
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
        this.jdbc = DB.getJdbcTemplate(dataSource);
        return (M) this;
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
        Long start = (this.pageNumber-1)*this.pageSize;
        limit.append(" limit ").append(start).append(", ").append(this.pageSize);
        return (M) this;
    }
    public M limit(Integer pageNum,Integer pageSize){
        this.pageNumber = Long.valueOf(pageNum);
        if (pageNum<1){
            this.pageNumber = 1L;
        }
        this.pageSize = pageSize;
        Long start = (this.pageNumber-1)*this.pageSize;
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


    public Map<String, Object> getAttrs(){
        return attrs;
    }
    private void beanToAttrs() throws IntrospectionException, InvocationTargetException, IllegalAccessException {
        Field[] fields = model.getClass().getDeclaredFields();
        for (Field field : fields) {
            PropertyDescriptor pd = new PropertyDescriptor(field.getName(),model.getClass());
            Method methodReader = pd.getReadMethod();
            Object value= methodReader.invoke(model);
            if (value != null) {
                String pf = model.calPrefix(field.getName());
                if (pf!=null){
                    String column = StringKit.toUnderscoreName(field.getName());
                    attrs.put(column, column+pf+value);
                }else {
                    attrs.put(StringKit.toUnderscoreName(field.getName()), value);
                }
                modifyFlag.add(StringKit.toUnderscoreName(field.getName()));
            }
        }
    }
    private String beanToColumns(Class<?> clazz){
        StringBuffer buffer = new StringBuffer();
        boolean flag =true;
        Field[] fields = clazz.getDeclaredFields();
        for (Field field:fields){
            String column = StringKit.toUnderscoreName(field.getName());
            if (flag){
                flag=false;
                buffer.append(column);
            }else {
                buffer.append(",");
                buffer.append(column);
            }
        }
        return buffer.toString();
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
            logger.error("插入异常",e);
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
     * @param conditions      操作条件，比如：conditions="userId=? and name=?" 必须是？号
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
     * @param conditions 比如 title=${title} and sub_name=${subName} or book=${?}
     * @param params 可以是 map 或者 bean
     * @return
     */
     public boolean delete(String conditions,Object params){
         String cdt = SqlXmlKit.autoAssembleSQL(conditions,params);
         return delete(cdt);
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
    public boolean deleteByIds(List list,String listColumn) {
        String st1=list.toString();
        String arr = st1.substring(st1.indexOf("[")+1,st1.indexOf("]"));
        StringBuilder sql = new StringBuilder();
        sql.append("delete from ");
        sql.append(table);
        sql.append(" where "+listColumn+" in ");
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
            logger.error("解析出错",e);
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
     * @param conditions 比如 title=${title} and sub_name=${subName} or book=${?}
     * @param params 可以是 map 或者 bean
     * @return
     */
    public boolean updateBy(String conditions,Object params){
        String cdt = SqlXmlKit.autoAssembleSQL(conditions,params);
        return updateBy(cdt);
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
     * 替换，如果没有这直接插入，否则替换
     * @param condition
     * @param paras
     * @return
     */
    public boolean replaceBy(String condition, Object[] paras){
        boolean rt = true;
        if (findBy(condition,paras,model.getClass()) == null){
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
     * @param conditions 比如 title=${title} and sub_name=${subName} or book=${?}
     * @param params 可以是 map 或者 bean
     * @return
     */
    public boolean replaceBy(String conditions,Object params){
        String cdt = SqlXmlKit.autoAssembleSQL(conditions,params);
        return replaceBy(cdt);
    }

    /**
     * 替换，如果没有这直接插入，否则替换
     * @param id
     * @return
     */
    public boolean replaceById(Object id){
        boolean rt = true;
        if (findById(id,model.getClass()) == null){
            rt = insert();
        }else {
            rt = updateById(id);
        }
        return rt;
    }
    public Long replaceAndReturnId(Long id){
        Long tId = id;
        if (findById(id,model.getClass()) == null){
            tId = insertAndReturnId();
        }else {
            updateById(id);
        }
        return tId;
    }

    /**
     * @param columns        字段名称，比如 columns="id,name,age"
     * @param conditions     conditions 查询条件，比如 conditions="user_id=? and age=?"
     * @param conditionParas 查询条件对应的参数
     * @return 返回Model对象
     * @
     */
    public <T> T findBy(String columns, String conditions, Object[] conditionParas,Class<T> clazz)  {
        List<T> result = findListBy(columns, conditions, conditionParas,clazz);
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
    public <T> T findBy(String conditions, Object[] conditionParas,Class<T> clazz)  {
        String columns = beanToColumns(clazz);
        return  findBy(columns,conditions,conditionParas,clazz);
    }
    /**
     * @param conditions conditions 查询条件，比如 conditions="user_id=? and age=?"
     * @return 返回Model对象
     * @
     */
    public <T> T findBy(String conditions,Class<T> clasz)  {
        return findBy(conditions,new Object[]{},clasz);
    }
    /**
     * 根据ID查询对象
     * @param id
     * @return
     */
    public <T> T findById(Object id,Class<T> clazz){
      return  findBy("id=?",new Object[]{id},clazz);
    }
    /**
     * 查找对象
     * @param conditions conditions 比如 title=${title} and sub_name=${subName} or book=${?}
     * @param params 可以是map 或bean
     * @return
     */
    public <T> T findBy(String conditions,Object params,Class<T> clazz){
        String cdt = SqlXmlKit.autoAssembleSQL(conditions,params);
        return findBy(cdt,clazz);
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
    protected  <T> List<T> queryList(String columns, String conditions, Object[] conditionParas, final Class<T> clazz)  {
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
                    Object mModel = clazz.newInstance();
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
                    logger.error("查询异常",e);
                }
                return null;
            }
        });
    }
    public <T> List<T> queryForList(String column, String conditions, Object[] conditionParas,Class<T> clazz){
        String sql = DefaultDialect.getDialect().forModelFindBy(table, column, " and "+conditions);
        if (order.length()>0){
            sql = sql+" "+order.toString();
        }
        if (limit.length()>0){
            sql = sql+" "+limit.toString();
        }
        if (!StringKit.isBaseDataType(clazz)){
            throw new DataException("类型不符合，只能使用基本类型");
        }
        return jdbc.queryForList(sql,conditionParas,clazz);
    }
    public <T> T queryForObject(String column, String conditions, Object[] conditionParas,Class<T> clazz){
        List<T> list = queryForList(column,conditions,conditionParas,clazz);
        if (list.size()==1){
            return  list.get(0);
        }else if (list.size()==0){
            return null;
        }
        throw new DataException("不止1条数据");
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
    public <T> List<T>  findListBy(String columns, String conditions, Object[] conditionParas,Class<T> clazz)  {
        return queryList(columns, " and "+conditions, conditionParas,clazz);
    }
    /**
     * 查找Model对象列表
     *
     * @param conditions     查询条件，比如 conditions="user_id=? and age=?"
     * @param conditionParas 查询条件对应的参数
     * @return 返回Model对象列表
     * @
     */
    public <T> List<T> findListBy(String conditions, Object[] conditionParas,Class<T> clazz)  {
        String columns = beanToColumns(clazz);
        return findListBy(columns, conditions, conditionParas,clazz);
    }
    /**
     * @param conditions 查询条件，比如 conditions="user_id=? and age=?"
     * @return 返回Model对象列表
     * @
     */
    public <T> List<T> findListBy(String conditions,Class<T> clazz)  {
        return findListBy(conditions, NULL_PARA_ARRAY,clazz);
    }



    /**
     * 查找对象列表
     * @param conditions conditions 比如 title=${title} and sub_name=${subName} or book=${?}
     * @param params 可以是map 或bean
     * @return
     */
    public <T> List<T> findListBy(String conditions,Object params,Class<T> clazz){
        String cdt = SqlXmlKit.autoAssembleSQL(conditions,params);
        return findListBy(cdt,clazz);
    }
    /**
     * 查找列表多少行
     * @param conditions
     * @param conditionParas
     * @return
     */
    public Long findListCountBy(String conditions, Object[] conditionParas){
        String sql = DefaultDialect.getDialect().forModelFindBy(table, " count(*) ", " and "+conditions);
        return  jdbc.queryForObject(sql,conditionParas, Long.class);
    }
    /**
     * 查找列表多少行
     * @param conditions conditions 比如 title=${title} and sub_name=${subName} or book=${?}
     * @param params 可以是map 或bean
     * @return
     */
    public Long findListCountBy(String conditions,Object params){
        String cdt = SqlXmlKit.autoAssembleSQL(conditions,params);
        String sql = "select count(*) from "+table+" where 1=1 and "+cdt;
        return jdbc.queryForObject(sql, NULL_PARA_ARRAY, Long.class);
    }


    /**
     * 分页查询
     * @param columns    字段名称，比如 columns="id,name,age"
     * @param conditions 查询条件，比如 conditions="user_id=? and age=?"
     * @param paras      查询参数
     * @return 返回对象列表
     * @ <T> T
     */
    public <T> Page<T>  paginate(String columns, String conditions, Object[] paras,Class<T> clazz)  {
        Long size = findListCountBy(conditions,paras);
        Long totalRow = size;
        if (totalRow == 0) {
            return new Page<T>(new ArrayList<T>(0), this.pageNumber, pageSize, 0L, 0L);
        }
        Long totalPage =  (totalRow / pageSize);
        if (totalRow % pageSize != 0) {
            totalPage++;
        }
        if (pageNumber > totalPage) {
            return new Page<T>(new ArrayList<T>(0), pageNumber, pageSize, totalPage, totalRow);
        }
        List<T> list =findListBy(columns, conditions, paras,clazz);
        return new Page<T>(list, pageNumber, pageSize, totalPage, totalRow);
    }

    /**
     * 分页查询
     * @param conditions conditions="user_id=? and age=?"
     * @param paras
     * @return
     */
    public <T> Page<T> paginate( String conditions, Object[] paras,Class<T> clazz)  {
        String columns = beanToColumns(clazz);
        return  paginate(columns,conditions,paras,clazz);
    }

    /**
     * 分页查询
     * @param conditions conditions 比如 title=${title} and sub_name=${subName} or book=${?}
     * @param params 可以是map 或bean
     * @return
     */
    public <T> Page<T> paginate(String conditions,Object params,Class<T> clazz)  {
        Long size = findListCountBy(conditions,params);
        Long totalRow = size;
        if (totalRow == 0) {
            return new Page<T>(new ArrayList<T>(0), this.pageNumber, pageSize, 0L, 0L);
        }
        Long totalPage =  (totalRow / pageSize);
        if (totalRow % pageSize != 0) {
            totalPage++;
        }
        if (pageNumber > totalPage) {
            return new Page<T>(new ArrayList<T>(0), pageNumber, pageSize, totalPage, totalRow);
        }
        List<T> list =findListBy(conditions,params,clazz);
        return new Page<T>(list, pageNumber, pageSize, totalPage, totalRow);
    }

}
