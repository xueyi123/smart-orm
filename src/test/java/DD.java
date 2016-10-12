import com.alibaba.fastjson.JSON;

import java.sql.Date;
import java.sql.Timestamp;

public class DD {
    public static void main(String[] args) {
        Date date = new Date(System.currentTimeMillis());
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        System.out.println(date.toString());
        System.out.println(timestamp.toString());
    }
}
