import com.iih5.smartorm.kit.SqlXmlKit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ---------------------------------------------------------------------------
 * 类名称 ：TestMain
 * 类描述 ：
 * 创建人 ： king.xue
 * 创建时间： 2017/1/4 15:17
 * 版权拥有：宇龙计算机通信科技.JV团队
 * ---------------------------------------------------------------------------
 */

public class TestMain {
    public static void main(String[] args) {
        Long lt = System.currentTimeMillis();
        //等号之间不能有空格，方法没有输入参数时，不能有空号比如：( )是错的
            String sql = " from t_book where bb=sub(now(),'2006.10.24') and count>${count} and author=${author} and id in (${list})";
            Map<String,Object> tokens = new HashMap<String,Object>();
            String d= null;
            tokens.put("author","wb");
            tokens.put("count",10008);
            tokens.put("title", d);
        List<Integer> list = new ArrayList<Integer>();
        list.add(100);
        list.add(1222);
        list.add(1);
        tokens.put("list",list);
        String vd=  SqlXmlKit.autoAssembleSQL(sql,tokens);


    }
}
