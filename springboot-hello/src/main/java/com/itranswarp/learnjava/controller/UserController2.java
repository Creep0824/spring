package com.itranswarp.learnjava.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

import com.itranswarp.learnjava.entity.User;
import com.itranswarp.learnjava.service.UserService;

import java.util.HashMap;
import java.util.Map;

@RestController
// 跨域配置
@CrossOrigin(origins = "http://localhost:8083")
public class UserController2 {

    public static final String KEY_USER = "__user__";
    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    UserService userService;

    // 修改为 @RequestBody 接收 JSON 数据
    @PostMapping("/register")
    public Map<String, Object> doRegister(@RequestBody Map<String, String> userData) {
        Map<String, Object> response = new HashMap<>();
        String email = userData.get("email");
        String password = userData.get("password");
        String name = userData.get("name");

        try {
            User user = userService.register(email, password, name);
            logger.info("User registered: {}", user.getEmail());
            response.put("success", true);
            response.put("message", "Registration successful");
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", "Registration failed: " + e.getMessage());
        }
        return response;
    }

    // 修改为 @RequestBody 接收 JSON 数据
    @PostMapping("/signin")
    public Map<String, Object> doSignin(@RequestBody Map<String, String> credentials, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        String email = credentials.get("email");
        String password = credentials.get("password");

        try {
            User user = userService.signin(email, password);
            session.setAttribute(KEY_USER, user);
            logger.info("User signed in success: {}", user.getEmail());
            response.put("success", true);
            response.put("message", "Signin successful");
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", "Signin failed: " + e.getMessage());
        }
        return response;
    }

    // 修改为 @RequestBody 接收 JSON 数据
    @PostMapping("/signout")
    public Map<String, Object> signout(@RequestBody Map<String, Object> requestData, HttpSession session) {
        session.removeAttribute(KEY_USER);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "signout successful");
        return response;
    }

    // 获取用户信息的接口也可以使用 @RequestBody 接收，但是由于只需要 session 中的用户信息，保持不变。
    @GetMapping("/profile")
    public Map<String, Object> profile(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        User user = (User) session.getAttribute(KEY_USER);
        if (user == null) {
            response.put("success", false);
            response.put("message", "User not signed in");
        } else {
            response.put("success", true);
            response.put("user", user);
        }
        return response;
    }

    // 获取所有用户信息接口可以保留
    @GetMapping("/")
    public void printAllUsers(HttpSession session) {
        userService.printAllUsers();
    }

}
