package life.majiang.community.controller;

import life.majiang.community.model.User;
import life.majiang.community.provider.GithubProvider;
import life.majiang.community.service.UserService;
import life.majiang.community.strategy.LoginUserInfo;
import life.majiang.community.strategy.UserStrategy;
import life.majiang.community.strategy.UserStrategyFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * Created by codedrinker on 2019/4/24.
 */
@Controller
@Slf4j
public class AuthorizeController {

    @Autowired
    private UserStrategyFactory userStrategyFactory;

    @Autowired
    private GithubProvider githubProvider;

    @Value("${github.client.id}")
    private String clientId;

    @Value("${github.client.secret}")
    private String clientSecret;

    @Value("${github.redirect.uri}")
    private String redirectUri;

    @Autowired
    private UserService userService;

    /**
     * OAuth2授权登录，调用第三方授权服务返回信息
     * @param type 登录方式
     * @param code 授权返回状态码
     * @param state 状态
     * @param request 请求
     * @param response 相应
     * @return 重定向到首页
     */
    @GetMapping("/callback/{type}")
    public String newCallback(@PathVariable(name = "type") String type,
                              @RequestParam(name = "code") String code,
                              @RequestParam(name = "state", required = false) String state,
                              HttpServletRequest request,
                              HttpServletResponse response) {
        // 1、判断登录方式 使用决策模式判断是gitee还是github登录
        UserStrategy userStrategy = userStrategyFactory.getStrategy(type);
        // 2、第三方授权获取用户登录信息
        LoginUserInfo loginUserInfo = userStrategy.getUser(code, state);
        if (loginUserInfo != null && loginUserInfo.getId() != null) {
            User user = new User();
            String token = UUID.randomUUID().toString();
            user.setToken(token);
            user.setName(loginUserInfo.getName());
            user.setAccountId(String.valueOf(loginUserInfo.getId()));
            user.setType(type);
            user.setAvatarUrl(loginUserInfo.getAvatarUrl());
            // 授权登录后添加用户信息
            userService.createOrUpdate(user);
            Cookie cookie = new Cookie("token", token);
            cookie.setMaxAge(60 * 60 * 24 * 30 * 6);
            cookie.setPath("/");
            response.addCookie(cookie);
            return "redirect:/";
        } else {
            log.error("callback get github error,{}", loginUserInfo);
            // 登录失败，重新登录
            return "redirect:/";
        }
    }

    /**
     * 退出登录，清空cookie数据，返回到首页
     * @param request 请求
     * @param response 响应
     * @return 重定向到首页
     */
    @GetMapping("/logout")
    public String logout(HttpServletRequest request,
                         HttpServletResponse response) {
        request.getSession().invalidate();
        Cookie cookie = new Cookie("token", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
        return "redirect:/";
    }
}
