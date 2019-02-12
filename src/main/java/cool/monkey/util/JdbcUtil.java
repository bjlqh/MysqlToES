package cool.monkey.util;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import javax.sql.DataSource;

public class JdbcUtil {

  public static DataSource dataSource = null;

  static {
    try {
      Properties properties = new Properties();
      properties.load(JdbcUtil.class.getClassLoader().getResourceAsStream("durid.properties"));
      dataSource = DruidDataSourceFactory.createDataSource(properties);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static DataSource getDataSource() {
    return dataSource;
  }

  public static Connection getConnection() throws SQLException {
    return dataSource.getConnection();
  }

  public static void close(AutoCloseable... closes) {
    for (AutoCloseable close : closes) {
      try {
        close.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
