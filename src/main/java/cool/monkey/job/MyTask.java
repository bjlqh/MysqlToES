package cool.monkey.job;

import java.io.IOException;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.client.RestHighLevelClient;

public class MyTask implements Runnable {

  private BulkRequest bulkRequest;
  private RestHighLevelClient esClient;

  public MyTask(BulkRequest bulkRequest, RestHighLevelClient esClient) {
    this.bulkRequest = bulkRequest;
    this.esClient = esClient;
  }

  @Override
  public void run() {
    try {
      long start = System.currentTimeMillis();
      esClient.bulk(bulkRequest);
      long end = System.currentTimeMillis();
      System.out.println("保存到es中用时：" + (end - start) / 1000 + "秒");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
