package cool.monkey.dao;

import cool.monkey.pojo.UserProfile;
import cool.monkey.util.JdbcUtil;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

public class UserProfileDao {

  JdbcTemplate jdbcTemplate = new JdbcTemplate(JdbcUtil.getDataSource());

  public UserProfile findUserProfile(long id) {
    String sql = "select up.id,up.thumb_photo_url from user_profiles up where up.id = ?";
    try {
      return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(UserProfile.class), id);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}
