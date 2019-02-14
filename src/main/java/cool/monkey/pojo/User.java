package cool.monkey.pojo;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

@Data
public class User implements Serializable {
  private long id;
  private String first_name;
  private String unique_name;
  private String birthday;
  private int deleted;
  private String gender;
  private String updated_at;
}
