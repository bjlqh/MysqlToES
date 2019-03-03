package cool.monkey.pojo;

import java.io.Serializable;
import lombok.Data;

@Data
public class User implements Serializable {

  private Long id;
  private String firstName;
  private String uniqueName;
  private String birthday;
  private Integer deleted;
  private String gender;
  private String updatedAt;
}
