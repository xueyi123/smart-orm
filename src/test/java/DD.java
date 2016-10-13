import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;

import java.sql.Date;
import java.sql.Timestamp;

public class DD {
    public static void main(String[] args) {
        BeanT t = new BeanT();
        t.setName("ddddddd");
        t.setId(1000);
        SimplePropertyPreFilter filter = new SimplePropertyPreFilter(BeanT.class, "name");

        System.out.println( JSON.toJSONString(t, filter));
    }
}
