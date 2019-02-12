package cool.monkey.dao;

import cool.monkey.pojo.User;
import cool.monkey.util.JdbcUtil;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

public class UserDao {

  JdbcTemplate jdbcTemplate = new JdbcTemplate(JdbcUtil.getDataSource());

  //条件分页查询
  public List<User> findList(int deleted, Date startTime, Date endTime, int pageNum, int pageSize) {
    int start = (pageNum - 1) * pageSize;
    List<Object> list = new ArrayList<>();
    list.add(deleted);
    list.add(startTime);
    list.add(endTime);
    list.add(start);
    list.add(pageSize);
    String sql = "select * from users u where u.deleted=? and u.updated_at between ? and ? limit ?,?";
    return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(User.class),list.toArray());

  }
}
