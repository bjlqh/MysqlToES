package cool.monkey.job;

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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequestInterceptor;
import org.elasticsearch.action.admin.indices.alias.Alias;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

public class SelectData {

  private static UserService userService = new UserService();
  private static String end = "2019-02-14 00:00:00";
  private static int pageNum = 1;
  private static int pageSize = 10000;

  private static String serviceName = "es";
  private static String region = "us-west-2";
  private static String aesEndpoint = "https://vpc-monkey-backend-sandbox-xwzormwdo6kgqux53yqaxwsvia.us-west-2.es.amazonaws.com";
  private static String index = "monkeydb";
  private static String type = "userIndex";

  static final AWSCredentialsProvider credentialsProvider = new DefaultAWSCredentialsProviderChain();


  public static void main(String[] args) throws ParseException, IOException {
    RestHighLevelClient esClient = esClient(serviceName, region);
    /*CreateIndexRequest indexRequest = new CreateIndexRequest(index).mapping(type);
    indexRequest.alias(new Alias("monkeydb_alias"));
    esClient.indices().create(indexRequest, RequestOptions.DEFAULT);*/
    System.out.println("=====================任务开始" + new Date() + "===================");
    long start1 = System.currentTimeMillis();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Date endTime = sdf.parse(end);
    ExecutorService pool = Executors.newFixedThreadPool(4);
    selecting(endTime, pageSize, esClient, pool, sdf);
    System.out.println("=====================任务结束" + new Date() + "===================");
    long end1 = System.currentTimeMillis();
    System.out.println("任务用时一共：" + (end1 - start1) / 1000 + "秒");
  }

  private static void selecting(Date endTime, int pageSize,
      RestHighLevelClient esClient, ExecutorService pool, SimpleDateFormat sdf)
      throws ParseException {

    while (true) {
      Map<String, Object> map = null;
      PageBean pageBean = null;
      long start = System.currentTimeMillis();
      map = userService.findList(endTime, pageSize, index, type);
      BulkRequest bulkRequest = (BulkRequest) map.get("bulkRequest");
      pageBean = (PageBean) map.get("pageBean");
      map.clear();
      int listSize = pageBean.getListSize();

      if (listSize > 0 && pageBean.getList() != null) {

        pool.execute(new MyTask(bulkRequest, esClient, listSize));
        String updatedAt = pageBean.getList().get(listSize - 1).getUpdatedAt();
        endTime = sdf.parse(updatedAt);
        long end = System.currentTimeMillis();
        System.out.println(
            "---------------第" + pageNum + "页用时：" + (end - start) / 1000 + "秒--------------");
        pageNum++;

        //跳出循环
        if (pageBean.getListSize() <= 0) {
          break;
        }
      }
    }
    pool.shutdown();
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
