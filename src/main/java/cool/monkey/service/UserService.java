package cool.monkey.service;

import com.alibaba.fastjson.JSON;
import cool.monkey.dao.UserDao;
import cool.monkey.dao.UserProfileDao;
import cool.monkey.pojo.PageBean;
import cool.monkey.pojo.User;
import cool.monkey.pojo.UserIndex;
import cool.monkey.pojo.UserProfile;
import cool.monkey.util.JsonUtil;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.common.xcontent.XContentType;

public class UserService {

  private UserDao userDao = new UserDao();
  private UserProfileDao userProfileDao = new UserProfileDao();

  public Map<String, Object> findList(Date endTime, int pageSize, String index, String type) {
    Map<String, Object> pageMap = new HashMap<>();
    Map<Long, UserIndex> userIndexMap = new HashMap<>();
    PageBean pageBean = new PageBean();
    BulkRequest bulkRequest = new BulkRequest();
    List<Long> ids = new ArrayList<>();

    long start = System.currentTimeMillis();
    List<User> list = userDao.findList(endTime, pageSize);
    long end = System.currentTimeMillis();
    System.out.println("查询user,用时：" + (end - start) / 1000 + "秒");

    pageBean.setListSize(list.size());
    pageBean.setList(list);
    for (User user : list) {
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
        //把userindex放入map中
        userIndexMap.put(id, userIndex);
      }
    }

    long start1 = System.currentTimeMillis();
    if (ids.size() > 0 && ids != null) {
      List<UserProfile> profiles = userProfileDao.findUserProfile(ids);
      long end1 = System.currentTimeMillis();
      System.out.println("查询userProfile,用时：" + (end1 - start1) / 1000 + "秒");

      long start2 = System.currentTimeMillis();
      for (UserProfile profile : profiles) {
        //从map中获取userindex
        UserIndex userIndex = userIndexMap.get(profile.getId());
        userIndex.setThumbPhotoUrl(profile.getThumbPhotoUrl());
        bulkRequest.add(generateUserRequest(userIndex, index, type));
      }
      long end2 = System.currentTimeMillis();
      System.out.println("userIndex转成json用时：" + (end2 - start2) / 1000 + "秒");
    }

    pageMap.put("pageBean", pageBean);
    pageMap.put("bulkRequest", bulkRequest);
    return pageMap;
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
