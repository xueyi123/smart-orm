import com.iih5.smartorm.cache.Redis;
import com.iih5.smartorm.generator.ModelGenerator;
import redis.clients.jedis.JedisPubSub;

public class MyMain {

    public static void main(String[] args) throws Exception {
        System.out.println("hello world");
        SpringContext.getInstace();

      //  ModelGenerator.generator("parkdb","com.tthd.model.generator","D:/ideaProject/smartorm/src/main/java");
        Redis.subscribe(new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                System.out.println("msg="+message);
            }
        },"test");
        for (int i = 0; i <1000 ; i++) {
            Redis.set("dd","dd");
        }
        Redis.publish("test","hahahah....");
        System.out.println( Redis.getNumIdlePool());
        System.out.println( Redis.getNumActivePool());
        System.out.println(Redis.getNumWaitersPool());


    }

}
