import com.alibaba.fastjson.JSON;
import com.iih5.smartorm.model.Db;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.swing.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MainTest {

    public static void main(String[] args) throws Exception {
        System.out.println("hello world");
        System.out.println("hello world");

//        UserModel test =new UserModel();
//        test.coin=8888;
//        test.nick_name="哈哈哈";
//        test.save();

        //List<VerifyCodeModel> modelList = model.find("select *from t_verify_code where id=?",new Object[]{11 });
       // VerifyCodeModel model = new VerifyCodeModel().findFirst("select *from t_verify_code where id=13");
      //  System.out.println(model.toString());


     // JdbcTemplate template=  SpringContext.getInstace().getBean("jdbcTemplate");
     //  VerifyCodeModel model= template.queryForObject("select *from t_verify_code where id=11",new VerifyCodeModel(),new Object[] {  });
     //   System.out.println(model.toString());

       // test(new VerifyCodeModel());

      //List<VerifyCodeModel> model= Db.findList("select *from t_verify_code",VerifyCodeModel.class);
       // System.out.println(model.toString());
       // VerifyCodeModel mmodel= new VerifyCodeModel();
       // mmodel.phone="120";
      //  mmodel.save();
        List<Object[]> list = new ArrayList<Object[]>();
        list.add(new Object[]{"99"});
        list.add(new Object[]{"0000"});
       // Db.batchUpdate("insert into t_verify_code (phone,varCode) values(?,?)",list);
        Db.update("delete from t_verify_code where varCode=?",new Object[]{"3905"});
       // Db.update("update t_verify_code set phone=?,varCode=? where id=?",new Object[]{"0000000","0000",24});


    }

}
