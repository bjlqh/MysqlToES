package cool.monkey.job;

import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.frameworkset.util.StringUtil;
import cool.monkey.pojo.User;
import cool.monkey.service.UserService;
import cool.monkey.interceptor.AWSRequestSigningApacheInterceptor;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequestInterceptor;
import org.elasticsearch.action.admin.indices.alias.Alias;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.frameworkset.elasticsearch.ElasticSearchHelper;
import org.frameworkset.elasticsearch.client.ClientInterface;

public class CreateUserIndex {

  private static String end;
  private static String start;
  private static int pageNum;
  private static int pageSize;
  private static String serviceName;
  private static String region;
  private static String aesEndpoint;
  private static String index;
  private static String type;
  private static String alias;
  private static UserService userService = new UserService();
  static final AWSCredentialsProvider credentialsProvider = new DefaultAWSCredentialsProviderChain();

  static {
    Properties properties = new Properties();
    try {
      properties
          .load(CreateUserIndex.class.getClassLoader().getResourceAsStream("config.properties"));
      end = properties.getProperty("end");
      start = properties.getProperty("start");
      pageNum = Integer.valueOf(properties.getProperty("pageNum"));
      pageSize = Integer.valueOf(properties.getProperty("pageSize"));
      serviceName = properties.getProperty("serviceName");
      region = properties.getProperty("region");
      aesEndpoint = properties.getProperty("aesEndpoint");
      index = properties.getProperty("index");
      type = properties.getProperty("type");
      alias = properties.getProperty("alias");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) throws ParseException, IOException {
    RestHighLevelClient esClient = esClient(serviceName, region);

    ClientInterface clientUtil = ElasticSearchHelper.getRestClientUtil();
    boolean isExistIndice = clientUtil.existIndice(index);

    if (!isExistIndice) {
      CreateIndexRequest indexRequest = new CreateIndexRequest(index);
      indexRequest.alias(new Alias(alias));
      try {
        esClient.indices().create(indexRequest, RequestOptions.DEFAULT);
      } catch (IOException e) {
        esClient.close();
        System.exit(1);
      }
    }

    System.out.println("=====================任务开始" + new Date() + "===================");
    long start1 = System.currentTimeMillis();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Date endTime = sdf.parse(end);
    Date startTime = null;
    if (!StringUtil.isEmpty(start)) {
      startTime = sdf.parse(start);
    }
    save(startTime, endTime, pageSize, pageNum, esClient, sdf, index, type);
    System.out.println("=====================任务结束" + new Date() + "===================");
    long end1 = System.currentTimeMillis();
    System.out.println("任务用时一共 ：" + (end1 - start1) / 1000 + "秒");
    esClient.close();
    System.exit(0);
  }

  private static void save(Date startTime, Date endTime, int pageSize, int pageNum,
      RestHighLevelClient esClient, SimpleDateFormat sdf, String index, String type)
      throws ParseException {

    while (true) {
      long start = System.currentTimeMillis();
      List<User> userList = userService
          .findList(endTime, pageSize, index, type, esClient);
      long end = System.currentTimeMillis();
      System.out.println("第" + pageNum + "页保存成功，" + "用时" + (end - start) / 1000 + "秒");
      pageNum++;

      if (startTime == null && userList.size() > 0) {
        String updatedAt = userList.get(userList.size() - 1).getUpdatedAt();
        endTime = sdf.parse(updatedAt);

      } else if (startTime != null && endTime.after(startTime)) {
        String updatedAt = userList.get(userList.size() - 1).getUpdatedAt();
        endTime = sdf.parse(updatedAt);

      } else {
        break;
      }
    }
  }

  private static RestHighLevelClient esClient(String serviceName, String region) {
    AWS4Signer signer = new AWS4Signer();
    signer.setServiceName(serviceName);
    signer.setRegionName(region);
    HttpRequestInterceptor interceptor = new AWSRequestSigningApacheInterceptor(serviceName, signer,
        credentialsProvider);
    return new RestHighLevelClient(RestClient.builder(HttpHost.create(aesEndpoint))
        .setHttpClientConfigCallback(hacb -> hacb.addInterceptorLast(interceptor)));
  }
}
