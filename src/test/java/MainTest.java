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


//        List<TableMeta> tableMetas= TableMetaTool.findTableMetaList("dataSource","parkdb");
//        for (TableMeta table:tableMetas) {
//            ModelGenerator.generator(table,"com.tthd.model.generator","D:/ideaProject/smartorm/src/main/java");
//        }

    }

}
