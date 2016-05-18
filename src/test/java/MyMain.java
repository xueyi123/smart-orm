import com.iih5.smartorm.generator.ModelGenerator;
import com.iih5.smartorm.model.Model;
import com.tthd.model.generator.UserModel;

public class MyMain {

    public static void main(String[] args) throws Exception {
        System.out.println("hello world");
        SpringContext.getInstace();
       // System.out.println("开始。。。");
       // ModelGenerator.generator("parkdb","com.tthd.model.generator","D:/ideaProject/smart-orm/src/main/java");
      //  System.out.println("。。。结束");

        UserModel userModel = new UserModel();

        userModel.setCoin((double) 99999);
        userModel.setNick_name("你大爷吗");
        userModel.update("id=?",new Object[]{10000002});

    }

}
