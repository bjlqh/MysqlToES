package cool.monkey.pojo;
import java.io.Serializable;
import lombok.Data;

@Data
public class UserIndex implements Serializable {
  private long id;
  private String firstName;
  private String uniqueName;
  private String birthday;
  private String gender;
  private String thumbPhotoUrl;
  private String updatedAt;
}
