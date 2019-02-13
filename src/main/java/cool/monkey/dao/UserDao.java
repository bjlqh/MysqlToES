package cool.monkey.dao;

import cool.monkey.pojo.User;
import cool.monkey.util.JdbcUtil;
import java.util.Date;
import java.util.List;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

public class UserDao {

  JdbcTemplate jdbcTemplate = new JdbcTemplate(JdbcUtil.getDataSource());

  //条件分页查询
  public List<User> findList(Date startTime, int pageSize) {
    String sql =
        "select u.id,u.first_name,u.unique_name,u.birthday,u.deleted,u.gender,u.updated_at from users u "
            + "where u.updated_at > ? order by u.updated_at asc limit ?";

    List<User> list = jdbcTemplate
        .query(sql, new BeanPropertyRowMapper<>(User.class), startTime, pageSize);
    return list;
  }
}
