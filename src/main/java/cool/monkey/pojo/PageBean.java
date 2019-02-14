package cool.monkey.pojo;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import lombok.Data;

@Data
public class PageBean implements Serializable {

  private List<User> list;
  private int listSize;
}
