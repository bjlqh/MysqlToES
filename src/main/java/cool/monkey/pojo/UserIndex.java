package cool.monkey.pojo;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

@Data
public class UserIndex implements Serializable {
  private long id;
  private String first_name;
  private String unique_name;
  private Date birthday;
  private String gender;
  private String thumb_photo_url;
  private Date updated_at;
}
