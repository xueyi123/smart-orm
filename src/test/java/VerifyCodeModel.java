import com.iih5.smartorm.model.Model;

import java.sql.Timestamp;

public class VerifyCodeModel extends Model<VerifyCodeModel> {
    public String phone;
    public String varCode;
    public Integer type;//不要用int
    public Timestamp create_time;
}
