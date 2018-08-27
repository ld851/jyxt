package com.ld.webclient.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ld.webclient.dao.LoginDao;
import com.ld.webclient.entity.User;
import java.util.List;

/**
 * 
 */
@Service
public class LoginService {

    @Autowired
    private LoginDao loginDao;

  public boolean verifyLogin(User user){

     List<User> userList = loginDao.findByUsernameAndPassword(user.getUsername(), user.getPassword());
      return userList.size()>0;
  }

}
