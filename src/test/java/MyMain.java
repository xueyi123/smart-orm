import com.iih5.smartorm.generator.ModelGenerator;

public class MyMain {

    public static void main(String[] args) throws Exception {
        System.out.println("hello world");
        SpringContext.getInstace();
        System.out.println("开始。。。");
        ModelGenerator.generator("parkdb","com.tthd.model.generator","D:/ideaProject/smart-orm/src/main/java");
        System.out.println("。。。结束");
    }

}
