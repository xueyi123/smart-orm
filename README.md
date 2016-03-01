#SmartORM使用说明
##这是一个ORM模型组件，可快速实现SQL操作(支持mysql)
##安装
依赖于spring环境下,在spring.xml配置\<bean class="com.iih5.smartorm.kit.SpringKit"/\>
##工作方式
##1基于Model操作数据库
<br>1 UserModel user =new UserModel();
<br>  user.coin=65800;
<br> user.nick_name="哈林";
<br>  user.save();
<br>2 VerifyCodeModel model = new VerifyCodeModel().find("select *from t_verify_code where id=13",VerifyCodeModel.class);
##2 基于Db操作数据库
<br>1 List<VerifyCodeModel> model= Db.findList("select *from t_verify_code",new Object[]{},VerifyCodeModel.class);
<br>2 Page<VerifyCodeModel> modelPage= Db.use("dataSource").paginate(VerifyCodeModel.class,1,6,"select *from t_verify_code",new Object[]{});
##Model自定生成器
<br> 参数说明：数据库名=packdb，包名=com.tthd.model.generator，java文件目录=D:/ideaProject/smartorm/src/main/java
<br>ModelGenerator.generator("parkdb","com.tthd.model.generator","D:/ideaProject/smartorm/src/main/java");
##代码强制规范
<br>1编写Model模型时，必须继承Model类
<br>2 Model的字段属性必须设置为public
<br>3 Model的字段必须保持和数据库里的字段相同
<br>4 Model模型命名采用驼峰式命名且名字和数据库表名保持一致映射，后缀最好为Model,比如表名t_user_info 对应的模型命名为UserInfoModel(忽略表前缀)

## --------------------------------------更新记录 -------------------------------------------------------------

<br>【1.0.1】 generator生成 生成器生成的Model 加入set get 方法 以及 serialVersion
<br>【1.0.1】 优化 Model 查询接口(接口参数也有所变动，注意更新)
<br>【1.0.1】 修改了一些Bug

##API查看地址
http://doc.iih5.com/orm/index.html


