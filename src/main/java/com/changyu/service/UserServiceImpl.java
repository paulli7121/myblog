package com.changyu.service;

import com.changyu.dao.UserRepository;
import com.changyu.po.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;

    @Override
    public User checkUser(String userName, String password) {
        User user = userRepository.findByUserNameAndPassword(userName, password);
        return user;
    }
}
