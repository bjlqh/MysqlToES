package cool.monkey.pojo;

import java.io.Serializable;
import lombok.Data;

@Data
public class UserProfile implements Serializable {

  private Long id;
  private String thumbPhotoUrl;
}
