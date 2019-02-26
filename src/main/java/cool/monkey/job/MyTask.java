package cool.monkey.job;

import java.io.IOException;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;

public class MyTask implements Runnable {

  private BulkRequest bulkRequest;
  private RestHighLevelClient esClient;
  private int listSize;

  public MyTask(BulkRequest bulkRequest, RestHighLevelClient esClient, int listSize) {
    this.bulkRequest = bulkRequest;
    this.esClient = esClient;
    this.listSize = listSize;
  }

  @Override
  public void run() {
    try {
      long start = System.currentTimeMillis();
      esClient.bulk(bulkRequest, RequestOptions.DEFAULT);
      long end = System.currentTimeMillis();
      System.out.println("保存到es中用时：" + (end - start) / 1000 + "秒");
    } catch (IOException e) {
      e.printStackTrace();
      System.out.println("异常退出！最后保存的user的id是：" + bulkRequest.requests().get(listSize - 1).id());
    }
  }
}
