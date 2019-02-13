package cool.monkey.pojo;

import java.io.Serializable;
import java.util.LinkedList;
import lombok.Data;

@Data
public class PageBean implements Serializable {

  private LinkedList<UserIndex> list;
  private int listSize;
}
