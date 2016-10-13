import com.alibaba.fastjson.JSON;
import com.iih5.smartorm.generator.ModelGenerator;
import com.tthd.model.generator.UserModel;

import java.util.List;

public class MyMain {

    public static void main(String[] args) throws Exception {
        System.out.println("hello world");
        SpringContext.getInstace();
        System.out.println("开始。。。");
      //  ModelGenerator.generator("TESTDB","com.tthd.model.generator","D:/IdeaProjects/smart-orm/src/main/java");

        BeanT beanT = new BeanT();
        beanT.setName("进军好莱坞");
        beanT.setId(2015);
        UserModel model = new UserModel(beanT);
        model.insert();
        System.out.println(JSON.toJSONString(model));

        System.out.println("。。。结束");


    }

}
