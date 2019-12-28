package com.xidian.community.controller;

import com.xidian.community.dto.AccessTokenDTO;
import com.xidian.community.dto.GithubUser;
import com.xidian.community.provider.GithubProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
public class AuthorizeController {

    @Autowired
    private GithubProvider githubProvider;

    @Value("${github.client.id}")
    private String clientId;
    @Value("${github.client.secret}")
    private String clientSecret;
    @Value("${github.redirect.url}")
    private String redirectUrl;

    @GetMapping("/callback")
    public String callback(@RequestParam(name = "code") String code, @RequestParam(name = "state") String state, HttpServletRequest request) {
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setCode(code);
        accessTokenDTO.setClient_id(clientId);
        accessTokenDTO.setState(state);
        accessTokenDTO.setRedirect_url(redirectUrl);
        accessTokenDTO.setClient_secret(clientSecret);
        String accesstoken = githubProvider.getAccessToken(accessTokenDTO);
        GithubUser user = githubProvider.getUser(accesstoken);
        if (user != null){
            //登录成功
            request.getSession().setAttribute("user",user);
            return "redirect:/";
        }
        else
        {
            //登录失败
            return "redirect:/";
        }
    }
}