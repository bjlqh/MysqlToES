/*package cool.monkey.job;

import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import cool.monkey.pojo.PageBean;
import cool.monkey.service.UserService;
import cool.monkey.util.AWSRequestSigningApacheInterceptor;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequestInterceptor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

public class SingleSelect {
  private static UserService userService = new UserService();
  private static String start = "2019-00-00 00:00:00";
  private static String end = "2019-00-00 00:00:00";
  private static int pageNum = 1;
  private static int pageSize = 10000;

  private static String serviceName = "es";
  private static String region = "us-west-2";
  private static String aesEndpoint = "";
  private static String index = "monkeydb";
  private static String type = "userIndex";

  static final AWSCredentialsProvider credentialsProvider = new DefaultAWSCredentialsProviderChain();


  public static void main(String[] args) throws ParseException {
    System.out.println("~~~~~~~任务开始" + new Date() + "~~~~~~~");
    long start1 = System.currentTimeMillis();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Date startTime = sdf.parse(start);
    Date endTime = sdf.parse(end);
    RestHighLevelClient esClient = esClient(serviceName, region);
    selecting(startTime, endTime, pageSize, esClient, sdf);
    System.out.println("~~~~~~~任务结束" + new Date() + "~~~~~~~");
    long end1 = System.currentTimeMillis();
    System.out.println("任务用时一共：" + (end1 - start1) / 1000 + "秒");
  }

  private static void selecting(Date startTime, Date endTime, int pageSize,
      RestHighLevelClient esClient, SimpleDateFormat sdf) throws ParseException {
    long start = System.currentTimeMillis();

    Map<String, Object> map = userService.findList(startTime, pageSize, index, type);
    BulkRequest bulkRequest = (BulkRequest) map.get("bulkRequest");
    PageBean pageBean = (PageBean) map.get("pageBean");

    if (pageBean.getListSize() > 0 && pageBean.getList() != null) {
      String updated_at = pageBean.getList().get(pageBean.getListSize() - 1).getUpdated_at();
      startTime = sdf.parse(updated_at);
      if (pageBean.getListSize() > 0 && endTime.after(startTime)) {
        try {
          long start1 = System.currentTimeMillis();
          esClient.bulk(bulkRequest);
          long end1 = System.currentTimeMillis();
          System.out.println("保存到es中用时：" + (end1 - start1) / 1000 + "秒");
        } catch (IOException e) {
          e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out
            .println("-------------第" + pageNum + "页用时：" + (end - start) / 1000 + "秒-------------");
        pageNum++;
        selecting(startTime, endTime, pageSize, esClient, sdf);
      }
    }
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
}*/
