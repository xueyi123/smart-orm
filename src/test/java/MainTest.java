import com.alibaba.fastjson.JSON;
import com.iih5.smartorm.generator.*;
import com.iih5.smartorm.model.Db;
import org.springframework.context.ApplicationContext;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainTest {

    public static void main(String[] args) throws Exception {
        System.out.println("hello world");
        System.out.println("hello world");
        ApplicationContext context= SpringContext.getInstace().getCtx();
     //   SpringKit.init(context);
//        UserModel test =new UserModel();
//        test.coin=8888;
//        test.nick_name="哈哈哈";
//        test.save();

        //List<VerifyCodeModel> modelList = model.find("select *from t_verify_code where id=?",new Object[]{11 });
       // VerifyCodeModel model = new VerifyCodeModel().findFirst("select *from t_verify_code where id=13");
      //  System.out.println(model.toString());


     // JdbcTemplate template=  SpringKit.getInstace().getBean("jdbcTemplate");
     //  VerifyCodeModel model= template.queryForObject("select *from t_verify_code where id=11",new VerifyCodeModel(),new Object[] {  });
     //   System.out.println(model.toString());

       // test(new VerifyCodeModel());

     // List<VerifyCodeModel> model= Db.findList("select *from t_verify_code",new Object[]{},VerifyCodeModel.class);
     //  System.out.println(model.toString());
       // VerifyCodeModel mmodel= new VerifyCodeModel();
       // mmodel.phone="120";
      //  mmodel.save();
       // Db.batchUpdate("insert into t_verify_code (phone,varCode) values(?,?)",list);
       // Db.update("delete from t_verify_code where varCode=?",new Object[]{"3905"});
       // Db.update("update t_verify_code set phone=?,varCode=? where id=?",new Object[]{"0000000","0000",24});

//        Page<VerifyCodeModel> modelPage= Db.paginate(VerifyCodeModel.class,1,6,"select *from t_verify_code",new Object[]{});
//        System.out.println(modelPage.toString());

        //String[] dbs = SpringKit.getApplicationContext().getBeanNamesForType(com.mchange.v2.c3p0.ComboPooledDataSource.class);
       // System.out.println(JSON.toJSONString(dbs));

        List<TableMeta> tableMetas= TableMetaTool.findTableMetaList("dataSource","parkdb");
        for (TableMeta table:tableMetas) {
            ModelGenerator.generator(table,"com.tthd.model.generator","D:/ideaProject/smartorm/src/main/java");
        }

    }

}
