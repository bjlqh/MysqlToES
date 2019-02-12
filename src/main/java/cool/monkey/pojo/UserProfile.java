package cool.monkey.pojo;

import java.io.Serializable;
import lombok.Data;

@Data
public class UserProfile implements Serializable {
  private long id;
  private String thumb_photo_url;
}
