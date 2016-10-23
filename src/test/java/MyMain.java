import com.alibaba.fastjson.JSON;
import com.iih5.smartorm.generator.GeneratorTool;
import com.iih5.smartorm.generator.ProjectType;
import com.iih5.smartorm.model.SortType;
import com.tthd.model.demo.TestTableModel;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;

public class MyMain {

    public static void main(String[] args) throws Exception {
        System.out.println("hello world");
        SpringContext.getInstace();
        System.out.println("开始。。。");
        //GeneratorTool.generator("com.tthd.model.demo", ProjectType.IDEA);

//        for (int i = 0; i < 10000; i++) {
//            long t = System.currentTimeMillis();
//            TestTableModel tm = new TestTableModel();
//            tm.setId(100000003240006L);
//            tm.setNickName("隔壁老王");
//            tm.setCreateTime(new Timestamp(System.currentTimeMillis()));
//            tm.setUpdateTime(new Timestamp(System.currentTimeMillis()));
//            tm.setTmpTime(new Date(System.currentTimeMillis()));
//            tm.setCash(new BigDecimal("1000.03"));
//            tm.setCount(10001.02F);
//            tm.setTotalCoin(20002D);
//            tm.setType(10);
//            tm.setMyTime(new Time(System.currentTimeMillis()));
//            tm.setBoolPara(true);
//            tm.setBiginterTt(new BigInteger("2000000001030304054"));
//            tm.insert();
//            System.out.println("time = "+(System.currentTimeMillis()-t));
//        }


//        TestTableModel model = new TestTableModel();
//       // model.incr("type",-1);
//        model.setCash(new BigDecimal("1001"));
//        model.setNickName("45345");
//        model.setBiginterTt(new BigInteger("666"));
//
//        BeanT beanT = new BeanT();
//        beanT.setId(10000000000005L);
      //  beanT.setNickName("45345");

        //model.replaceBy(beanT);
        //model.replaceBy("id=10000000000005");
        List<TestTableModel> model = new TestTableModel().limit(0L,100).order("update_time", SortType.ASC).findListBy("id=?",new Object[]{new BigInteger("55555588888")});
        System.out.println(JSON.toJSONString(model));
//        List<String> list =  new TestTableModel().limit(0L,100).queryForList("nick_name","id=?",new Object[]{new BigInteger("100000003240006")},String.class);
//        System.out.println(JSON.toJSONString(list));
    //    String name =  new TestTableModel().queryForObject("nick_name","id=?",new Object[]{new BigInteger("10000000000002")},String.class);
    //    System.out.println(name);
//         TestTableModel testTableModel = new TestTableModel();
//         testTableModel.setId(55555588888L);
//         testTableModel.updateById(new BigInteger("100000003240006"));
//
        //Long longd = new TestTableModel().findListCountBy("id=?",new Object[]{10000000000005L});
       // System.out.println("---"+longd);
        //System.out.println("。。。结束");
    }

}
