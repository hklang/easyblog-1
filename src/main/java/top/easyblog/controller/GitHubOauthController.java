package top.easyblog.controller;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import top.easyblog.bean.User;
import top.easyblog.bean.UserSigninLog;
import top.easyblog.bean.oauth2.GitHubUser;
import top.easyblog.common.enums.oauth2.AppType;
import top.easyblog.common.util.CookieUtils;
import top.easyblog.common.util.NetWorkUtils;
import top.easyblog.common.util.RedisUtils;
import top.easyblog.common.util.UserUtils;
import top.easyblog.service.IAuthService;
import top.easyblog.service.IOauthService;
import top.easyblog.service.IUserService;
import top.easyblog.service.impl.UserSigninLogServiceImpl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.Executor;

/**
 * 提供各种登录认证的控制器
 *
 * @author ：huangxin
 * @modified ：
 * @since ：2020/05/27 23:58
 */
@Slf4j
@Controller
@RequestMapping("/oauth/github")
public class GitHubOauthController {

    @Autowired
    private IAuthService<GitHubUser> githubAuthService;
    @Autowired
    private IOauthService<GitHubUser> oauthService;
    @Autowired
    private UserSigninLogServiceImpl userSigninLogService;
    @Autowired
    private IUserService userService;
    @Autowired
    RedisUtils redisUtil;
    @Autowired
    private Executor executor;

    private static final String LOGIN_PAGE = "redirect:/user/loginPage";

    private static final String USER_LOGIN_COOKIE_FLAG = "USER-INFO";


    /**
     * 让用户跳转到 GitHub授权页面
     *
     * @return 跳转url
     */
    @GetMapping(value = "/")
    public String authorize() {
        String url = githubAuthService.getAuthorizationUrl();
        log.info("Authorize url:{}", url);
        return "redirect:" + url;
    }


    /**
     * GitHub回调接口：当用户同一授权之后github调用此接口，用于返回到系统的指定页面
     *
     * @param code github回调的时候返回的授权码（使用过后据失效）
     * @return 指定页面
     */
    @GetMapping("/callback")
    public String callback(@RequestParam(value = "code") String code,
                           HttpServletRequest request,
                           HttpServletResponse response,
                           RedirectAttributes redirectAttributes) {
        String accessToken = githubAuthService.getAccessToken(code);
        //获得用户在Github上的账户信息
        GitHubUser gitHubUser = githubAuthService.getUserInfo(accessToken);
        //在本系统中尝试获取用户的信息和GitHub返回的信息对比
        GitHubUser user = oauthService.getUserByOpenIdAndAppType(gitHubUser.getId(), AppType.GitHub);
        String ip = NetWorkUtils.getUserIp(request);
        String ipInfo = NetWorkUtils.getLocation(request, ip);
        Integer registerId = -1;
        User userInfo = null;
        //和系统绑定
        if (user == null) {
            //用户首次在系统使用GitHub登录
            User newUser = new User();
            newUser.setUserNickname(gitHubUser.getLogin());
            newUser.setUserHeaderImgUrl(gitHubUser.getAvatarUrl());
            newUser.setUserPassword("");
            newUser.setUserRegisterIp(ip + " " + ipInfo);
            newUser.setUserFreeze(0);
            newUser.setUserLock(0);
            newUser.setUserPower(3);
            userService.registerByThirdPart(newUser);
            //返回用户的Id,MyBatis把自增Id返回在对象中
            registerId = newUser.getUserId();
            int register = oauthService.register(gitHubUser, registerId);
            userInfo = userService.getUser(registerId);
            if (register > 0) {
                log.info("用户 {} 注册成功 ,{}", registerId, LocalDateTime.now());
            } else {
                log.info("用户 {} 注册失败 ,{}", ip + " " + ipInfo, LocalDateTime.now());
                redirectAttributes.addFlashAttribute("error", "服务异常，请重试！");
                return LOGIN_PAGE;
            }
        }

        //根据用户绑定的用户Id查询用户信息
        if(registerId==-1){
            assert user != null;
            registerId=Integer.parseInt(user.getId());
            userInfo=userService.getUser(registerId);
        }

        //第三方登录
        if (Objects.isNull(UserUtils.getUserFromRedis(registerId))) {
            redisUtil.hset("user-" + registerId, "user", JSONObject.toJSONString(userInfo), RedisUtils.DB_1);
            //会话信息，如果没有主动退出15天有效
            redisUtil.expire("user-" + registerId, 60 * 60 * 24 * 15, RedisUtils.DB_1);
        }
        if (Objects.isNull(CookieUtils.getCookieValue(request, USER_LOGIN_COOKIE_FLAG))) {
            //添加用户的登录信息到Cookie中
            CookieUtils.addCookie(request, response, USER_LOGIN_COOKIE_FLAG, JSONObject.toJSONString(userInfo), 60 * 60 * 24 * 15);
        }
        final int uid = registerId;
        executor.execute(() -> {
            userSigninLogService.saveSigninLog(new UserSigninLog(uid, ip, ipInfo, "登录成功"));
        });
        String refererUrl = (String) redisUtil.get("Referer-" + ip, RedisUtils.DB_1);
        if (Objects.nonNull(refererUrl) && !"".equals(refererUrl)) {
            //在每次取登录界面的时候都会在Redis中记录登录之前访问的页面Referer，登录成功后删除对应的Referer
            redisUtil.delete(RedisUtils.DB_1, "Referer-" + ip);
            log.info("redirect to : {}", refererUrl);
            return "redirect:" + refererUrl;
        }
        log.info("redirect to : index");
        return "redirect:/";
    }



}