package cool.monkey.job;

import com.alibaba.fastjson.JSON;
import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import cool.monkey.pojo.PageBean;
import cool.monkey.pojo.UserIndex;
import cool.monkey.service.UserService;
import cool.monkey.util.AWSRequestSigningApacheInterceptor;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequestInterceptor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;

public class ToEs {

  private static UserService userService = new UserService();
  private static int deleted = 1;
  private static String start = "2018-08-01 00:00:00";
  private static String end = "2018-12-01 00:00:00";
  private static int pageNum = 1;
  private static int pageSize = 100;

  private static String serviceName = "es";
  private static String region = "us-west-2";
  private static String aesEndpoint = "https://vpc-monkey-backend-sandbox-xwzormwdo6kgqux53yqaxwsvia.us-west-2.es.amazonaws.com";
  private static String index = "monkeydb";
  private static String type = "userIndex";

  static final AWSCredentialsProvider credentialsProvider = new DefaultAWSCredentialsProviderChain();

  public static void main(String[] args) throws ParseException {
    System.out.println("=====================任务开始" + new Date() + "===================");
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Date startTime = sdf.parse(start);
    Date endTime = sdf.parse(end);

    RestHighLevelClient esClient = esClient(serviceName, region);
    BulkRequest bulkRequest = new BulkRequest();

    while (true) {
      PageBean pageBean = userService.findList(deleted, startTime, endTime, pageNum, pageSize);
      System.out.println("==========="+pageBean);
      List<IndexRequest> requests = generateRequest(pageBean.getList());
      for (IndexRequest request : requests) {
        bulkRequest.add(request);
      }

      try {
        esClient.bulk(bulkRequest);
      } catch (IOException e) {
        e.printStackTrace();
      }

      pageNum++;
      if (pageBean.getListSize() <= 0) {
        break;
      }
    }
    System.out.println("=====================任务结束" + new Date() + "===================");
  }

  public static List<IndexRequest> generateRequest(List<UserIndex> list) {
    List<IndexRequest> requests = new ArrayList<>();
    for (UserIndex userIndex : list) {
      requests.add(generateUserRequest(userIndex));
    }
    return requests;
  }

  private static IndexRequest generateUserRequest(UserIndex userIndex) {
    IndexRequest request = new IndexRequest(index, type, String.valueOf(userIndex.getId()));
    String jsonString = JSON.toJSONString(userIndex);
    request.source(jsonString, XContentType.JSON);
    return request;
  }

  public static RestHighLevelClient esClient(String serviceName, String region) {
    AWS4Signer signer = new AWS4Signer();
    signer.setServiceName(serviceName);
    signer.setRegionName(region);
    HttpRequestInterceptor interceptor = new AWSRequestSigningApacheInterceptor(serviceName, signer,
        credentialsProvider);
    return new RestHighLevelClient(RestClient.builder(HttpHost.create(aesEndpoint))
        .setHttpClientConfigCallback(hacb -> hacb.addInterceptorLast(interceptor)));
  }
}