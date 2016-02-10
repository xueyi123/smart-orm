#SmartORM使用说明
##这是一个ORM模型组件，可快速实现SQL操作(目前只支持mysql)
##安装
###在spring.xml配置<bean class="com.iih5.smartorm.kit.SpringKit"/>
##依赖于spring环境下
##工作方式
##1基于Model操作数据库
###1UserModel user =new UserModel();
###user.coin=65800;
###user.nick_name="哈林";
###user.save();
###2 VerifyCodeModel model = new VerifyCodeModel().find("select *from t_verify_code where id=13",VerifyCodeModel.class);
##2 基于Db操作数据库
###1List<VerifyCodeModel> model= Db.findList("select *from t_verify_code",new Object[]{},VerifyCodeModel.class);
###2Page<VerifyCodeModel> modelPage= Db.use("JdbcBeanId").paginate(VerifyCodeModel.class,1,6,"select *from t_verify_code",new Object[]{});
##代码强制规范
###1编写Model模型时，必须继承Model类
###2Model的字段属性必须设置为public
###3Model的字段必须保持和数据库里的字段相同
###4Model模型命名采用驼峰式命名且名字和数据库表名保持一致映射，后缀最好为Model,比如表名t_user_info 对应的模型命名为UserInfoModel(忽略表前缀)



