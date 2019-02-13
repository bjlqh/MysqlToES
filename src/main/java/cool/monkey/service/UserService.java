package cool.monkey.service;

import cool.monkey.dao.UserDao;
import cool.monkey.dao.UserProfileDao;
import cool.monkey.pojo.PageBean;
import cool.monkey.pojo.User;
import cool.monkey.pojo.UserIndex;
import cool.monkey.pojo.UserProfile;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class UserService {

  private UserDao userDao = new UserDao();
  private UserProfileDao userProfileDao = new UserProfileDao();

  public PageBean findList(Date startTime, int pageSize) {

    List<User> list = userDao.findList(startTime, pageSize);
    PageBean pageBean = new PageBean();
    LinkedList<UserIndex> userIndices = new LinkedList<>();
    pageBean.setListSize(list.size());

    for (User user : list) {
      UserIndex userIndex = new UserIndex();
      long id = user.getId();
      userIndex.setId(id);
      userIndex.setFirst_name(user.getFirst_name());
      userIndex.setUnique_name(user.getUnique_name());
      userIndex.setGender(user.getGender());
      userIndex.setBirthday(user.getBirthday());
      userIndex.setUpdated_at(user.getUpdated_at());
      UserProfile userProfile = userProfileDao.findUserProfile(id);
      if (userProfile != null) {
        userIndex.setThumb_photo_url(userProfile.getThumb_photo_url());
      }
      userIndices.add(userIndex);
    }
    pageBean.setList(userIndices);
    return pageBean;
  }
}
