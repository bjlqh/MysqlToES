package cool.monkey.pojo;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

@Data
public class PageBean implements Serializable {
  private List<UserIndex> list;
  private int listSize;
}
