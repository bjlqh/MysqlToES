package cool.monkey.pojo;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

@Data
public class User implements Serializable {
  private long id;
  private String firstName;
  private String uniqueName;
  private String birthday;
  private int deleted;
  private String gender;
  private String updatedAt;
}
