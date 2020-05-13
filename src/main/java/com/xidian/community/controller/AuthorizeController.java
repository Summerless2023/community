package com.xidian.community.controller;

import com.xidian.community.config.GithubConfig;
import com.xidian.community.mapper.UserMapper;
import com.xidian.community.model.User;
import com.xidian.community.model.dto.AccessTokenDTO;
import com.xidian.community.model.dto.GithubUser;
import com.xidian.community.provider.GithubProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Controller
public class AuthorizeController {

    @Autowired
    private GithubProvider githubProvider;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private GithubConfig githubConfig;

//    @Value("${github.client.id}")
//    private String clientId;
//    @Value("${github.client.secret}")
//    private String clientSecret;
//    @Value("${github.redirect.url}")
//    private String redirectUrl;


    @GetMapping("/callback")
    public String callback(@RequestParam(name = "code") String code, @RequestParam(name = "state") String state, HttpServletRequest request, HttpServletResponse response) {
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setCode(code);
//        accessTokenDTO.setClient_id(clientId);
        accessTokenDTO.setClient_id(githubConfig.getClientId());
        accessTokenDTO.setState(state);
//        accessTokenDTO.setRedirect_url(redirectUrl);
//        accessTokenDTO.setClient_secret(clientSecret);
        accessTokenDTO.setRedirect_url(githubConfig.getRedirectUrl());
        accessTokenDTO.setClient_secret(githubConfig.getClientSecret());
        String accesstoken = githubProvider.getAccessToken(accessTokenDTO);
        GithubUser githubUser = githubProvider.getUser(accesstoken);
        if (githubUser != null){
            //登录成功
            User user = new User();
            String token = UUID.randomUUID().toString();
            user.setToken(token);
            user.setName(githubUser.getName());
            user.setAccountId(String.valueOf(githubUser.getId()));
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            System.out.println(user);
            userMapper.insert(user);
            response.addCookie(new Cookie("token",token));
            return "redirect:/";
        }
        else
        {
            //登录失败
            return "redirect:/";
        }
    }
}