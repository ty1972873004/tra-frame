package com.javaxxw.manager.controller.sso;

import com.javaxxw.base.controller.BaseController;
import com.javaxxw.common.config.Resources;
import com.javaxxw.common.constants.WebConstants;
import com.javaxxw.redis.service.JedisClient;
import com.javaxxw.redis.service.RedisUtil;
import com.javaxxw.shiro.session.TraSession;
import com.javaxxw.shiro.session.TraSessionDao;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.UUID;

/**
 * @author tuyong
 * @version 1.0
 * @DESCRIPTION
 * @create 2017-06-19 11:13
 **/
@Controller
@RequestMapping("/sso")
@Api(value = "单点登录管理", description = "单点登录管理")
public class SSOController extends BaseController {

    @Autowired
    private JedisClient jedisClient;

    @Autowired
    private TraSessionDao traSessionDao;


    private static Logger logger = LogManager.getLogger(SSOController.class);

    // 全局会话key
    private final static String TRA_MANAGER_SERVER_SESSION_ID = "tra-manager-server-session-id";
    // 全局会话key列表
    private final static String TRA_MANAGER_SERVER_SESSION_IDS = "tra-manager-server-session-ids";
    // code key
    private final static String TRA_MANAGER_SERVER_CODE = "tra-manager-server-code";

    @ApiOperation(value = "认证中心首页")
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String index(HttpServletRequest request) throws Exception {

        String appid = request.getParameter("appid");
        String backurl = request.getParameter("backurl");
        if (StringUtils.isBlank(appid)) {
            throw new RuntimeException("无效访问！");
        }


        return "redirect:/sso/login?backurl=" + URLEncoder.encode(backurl, "utf-8");
    }

    @ApiOperation(value = "登录")
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(HttpServletRequest request) {
        Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        String serverSessionId = session.getId().toString();
        // 判断是否已登录，如果已登录，则回跳
        String code = (String)jedisClient.get(TRA_MANAGER_SERVER_SESSION_ID + "_" + serverSessionId);
        // code校验值
        if (StringUtils.isNotBlank(code)) {
            // 回跳
            String backurl = request.getParameter("backurl");
            String username = (String) subject.getPrincipal();
            if (StringUtils.isBlank(backurl)) {
                backurl = "/";
            } else {
                if (backurl.contains("?")) {
                    backurl += "&tra_code=" + code + "&tra_username=" + username;
                } else {
                    backurl += "?tra_code=" + code + "&tra_username=" + username;
                }
            }
            logger.debug("认证中心帐号通过，带code回跳：{}", backurl);
            return "redirect:" + backurl;

        }
        return "/sso/login.jsp";
    }

    @ApiOperation(value = "退出登录")
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(HttpServletRequest request) {
        // shiro退出登录
        SecurityUtils.getSubject().logout();
        // 跳回原地址
        String redirectUrl = request.getHeader("Referer");
        if (null == redirectUrl) {
            redirectUrl = "/";
        }
        return "redirect:" + redirectUrl;
    }

    @ApiOperation(value = "登录")
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public Object login(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String rememberMe = request.getParameter("rememberMe");

        Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        String sessionId = session.getId().toString();
        // 判断是否已登录，如果已登录，则回跳，防止重复登录
        //String hasCode = RedisUtil.get(TRA_MANAGER_SERVER_SESSION_ID + "_" + sessionId);
        String hasCode = (String)jedisClient.get(TRA_MANAGER_SERVER_SESSION_ID + "_" + sessionId);
        // code校验值
        if (StringUtils.isBlank(hasCode)) {
            // 使用shiro认证
            UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(username, password);
            try {
                if (BooleanUtils.toBoolean(rememberMe)) {
                    usernamePasswordToken.setRememberMe(true);
                } else {
                    usernamePasswordToken.setRememberMe(false);
                }
                subject.login(usernamePasswordToken);
            } catch (UnknownAccountException e) {
                return setModelMap(modelMap,WebConstants.ACCOUNT_NOT_EXIST);
            } catch (IncorrectCredentialsException e) {
                return setModelMap(modelMap,WebConstants.PASSWORD_ERROR);
            } catch (LockedAccountException e) {
                return setModelMap(modelMap,WebConstants.ACCOUNT_IS_LOCKED);
            }

            // 更新session状态
            traSessionDao.updateStatus(sessionId, TraSession.OnlineStatus.on_line);

            // 全局会话sessionId列表，供会话管理
            //RedisUtil.lpush(TRA_MANAGER_SERVER_SESSION_IDS, sessionId.toString());
            jedisClient.lpush(TRA_MANAGER_SERVER_SESSION_IDS, sessionId.toString());
            // 默认验证帐号密码正确，创建code
            String code = UUID.randomUUID().toString();
            // 全局会话的code
            //RedisUtil.set(TRA_MANAGER_SERVER_SESSION_ID + "_" + sessionId, code, (int) subject.getSession().getTimeout() / 1000);
            jedisClient.set(TRA_MANAGER_SERVER_SESSION_ID + "_" + sessionId, code, (int) subject.getSession().getTimeout() / 1000);
            // code校验值
            //RedisUtil.set(TRA_MANAGER_SERVER_CODE + "_" + code, code, (int) subject.getSession().getTimeout() / 1000);
            jedisClient.set(TRA_MANAGER_SERVER_CODE + "_" + code, code, (int) subject.getSession().getTimeout() / 1000);
        }
        // 回跳登录前地址
        String backurl = request.getParameter("backurl");

        if (StringUtils.isBlank(backurl)) {
            return setModelMap(modelMap,WebConstants.REQUEST_SUCCESS,"/");
        }else {
            return setModelMap(modelMap,WebConstants.REQUEST_SUCCESS,backurl);
        }
    }

}
