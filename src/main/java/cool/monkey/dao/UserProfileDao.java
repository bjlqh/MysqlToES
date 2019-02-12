package cool.monkey.dao;

import cool.monkey.pojo.UserProfile;
import cool.monkey.util.JdbcUtil;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

public class UserProfileDao {

  JdbcTemplate jdbcTemplate = new JdbcTemplate(JdbcUtil.getDataSource());

  public UserProfile findUserProfile(long id) {
    String sql = "select * from user_profiles up where up.id = ?";
    return jdbcTemplate.queryForObject(sql,new BeanPropertyRowMapper<>(UserProfile.class),id);
  }
}
