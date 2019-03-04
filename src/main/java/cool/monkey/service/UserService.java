package cool.monkey.service;

import cool.monkey.dao.UserDao;
import cool.monkey.dao.UserProfileDao;
import cool.monkey.pojo.User;
import cool.monkey.pojo.UserIndex;
import cool.monkey.pojo.UserProfile;
import cool.monkey.util.JsonUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;

public class UserService {

  private UserDao userDao = new UserDao();
  private UserProfileDao userProfileDao = new UserProfileDao();

  public List<User> findList(Date endTime, int pageSize, String index, String type,
      RestHighLevelClient esClient) {

    Map<Long, UserIndex> userIndexMap = new HashMap<>();
    BulkRequest bulkRequest = new BulkRequest();
    List<Long> ids = new ArrayList<>();

    long start = System.currentTimeMillis();
    List<User> userList = userDao.findList(endTime, pageSize);
    long end = System.currentTimeMillis();
    System.out.println("查询user用时：" + (end - start) / 1000 + "秒");

    for (User user : userList) {
      if (user.getDeleted() == 0) {
        UserIndex userIndex = new UserIndex();
        long id = user.getId();
        ids.add(id);
        userIndex.setId(id);
        userIndex.setFirstName(user.getFirstName());
        userIndex.setUniqueName(user.getUniqueName());
        userIndex.setGender(user.getGender());
        userIndex.setBirthday(user.getBirthday());
        userIndex.setUpdatedAt(user.getUpdatedAt());
        userIndexMap.put(id, userIndex);
      }
    }

    if (userList.size() > 0) {
      long start1 = System.currentTimeMillis();
      List<UserProfile> userProfileList = userProfileDao.findUserProfileList(ids);
      long end1 = System.currentTimeMillis();
      System.out.println("查询userProfile用时：" + (end1 - start1) / 1000 + "秒");

      long start2 = System.currentTimeMillis();
      for (UserProfile userProfile : userProfileList) {
        UserIndex userIndex = userIndexMap.get(userProfile.getId());
        userIndex.setThumbPhotoUrl(userProfile.getThumbPhotoUrl());
        bulkRequest.add(generateUserRequest(userIndex, index, type));
      }
      long end2 = System.currentTimeMillis();
      System.out.println("添加到批量集合bulk用时：" + (end2 - start2) / 1000 + "秒");
      long start3 = System.currentTimeMillis();

      try {
        esClient.bulk(bulkRequest, RequestOptions.DEFAULT);
      } catch (IOException e) {
        System.out.println("保存error，保存的上一个updatedAt是:" + endTime);
        System.exit(1);
      }
      long end3 = System.currentTimeMillis();
      System.out
          .println("保存" + userIndexMap.keySet().size() + "条记录用时：" + (end3 - start3) / 1000 + "秒");

    }
    return userList;
  }

  private IndexRequest generateUserRequest(UserIndex userIndex, String index, String type) {
    IndexRequest request = new IndexRequest(index, type).id(String.valueOf(userIndex.getId()));
    Optional<String> optional = JsonUtil.toJson(userIndex);
    if (optional.isPresent()) {
      String jsonString = optional.get();
      request.source(jsonString, XContentType.JSON);
    }
    return request;
  }
}
