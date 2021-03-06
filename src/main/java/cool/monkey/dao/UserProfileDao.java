package cool.monkey.dao;

import cool.monkey.pojo.UserProfile;
import cool.monkey.util.JdbcUtil;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

public class UserProfileDao {

  JdbcTemplate jdbcTemplate = new JdbcTemplate(JdbcUtil.getDataSource());

  public List<UserProfile> findUserProfileList(List<Long> ids) {
    if (ids.size() > 0) {
      String id = Arrays.toString(ids.toArray()).replace("[", "").replace("]", "");
      String sql =
          "select up.id,up.thumb_photo_url thumbPhotoUrl from user_profiles up where up.id in ("
              + id + ")";
      return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(UserProfile.class));
    } else {
      return Collections.emptyList();
    }
  }
}
