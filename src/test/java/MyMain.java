import com.iih5.smartorm.generator.ModelGenerator;

public class MyMain {

    public static void main(String[] args) throws Exception {
        System.out.println("hello world");
        SpringContext.getInstace();

        ModelGenerator.generator("parkdb","com.tthd.model.generator","D:/ideaProject/smartorm/src/main/java");
    }

}
