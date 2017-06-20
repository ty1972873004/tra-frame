package com.javaxxw.shiro.filter;

import com.alibaba.fastjson.JSONObject;
import com.javaxxw.common.constants.Constants;
import com.javaxxw.common.utils.PropertiesFileUtil;
import com.javaxxw.redis.service.JedisClient;
import com.javaxxw.redis.service.RedisUtil;
import com.javaxxw.shiro.session.TraSessionDao;
import com.javaxxw.shiro.util.RequestParameterUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.AuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * @desc 重写authc过滤器
 * @author tuyong
 * @since 2017/6/16
 * @version 1.0
 */
public class TraAuthenticationFilter extends AuthenticationFilter {

    private static Logger logger = LogManager.getLogger(TraAuthenticationFilter.class);

    // 局部会话key
    private final static String TRA_MANAGER_CLIENT_SESSION_ID = "tra-manager-client-session-id";
    // 单点同一个code所有局部会话key
    private final static String TRA_MANAGER_CLIENT_SESSION_IDS = "tra-manager-client-session-ids";

    @Autowired
    TraSessionDao traSessionDao;

    @Autowired
    JedisClient jedisClient;

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        Subject subject = getSubject(request, response);
        Session session = subject.getSession();
        // 判断请求类型
        String traType = PropertiesFileUtil.getInstance("tra-manager-client").get("tra.manager.type");

        session.setAttribute(Constants.MANAGER_TYPE, traType);
        if ("client".equals(traType)) {
            return validateClient(request, response);
        }
        if ("server".equals(traType)) {
            return subject.isAuthenticated();
        }
        return false;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        StringBuffer sso_server_url = new StringBuffer(PropertiesFileUtil.getInstance("tra-manager-client").get("tra.manager.sso.server.url"));
        // server需要登录
        String traType = PropertiesFileUtil.getInstance("tra-manager-client").get("tra.manager.type");
        if ("server".equals(traType)) {
            WebUtils.toHttp(response).sendRedirect(sso_server_url.append("/sso/login").toString());
            return false;
        }
        sso_server_url.append("/sso/index").append("?").append("appid").append("=").append(PropertiesFileUtil.getInstance("tra-manager-client").get("tra.manager.appID"));
        // 回跳地址
        HttpServletRequest httpServletRequest = WebUtils.toHttp(request);
        StringBuffer backurl = httpServletRequest.getRequestURL();
        String queryString = httpServletRequest.getQueryString();
        if (StringUtils.isNotBlank(queryString)) {
            backurl.append("?").append(queryString);
        }
        sso_server_url.append("&").append("backurl").append("=").append(URLEncoder.encode(backurl.toString(), "utf-8"));
        WebUtils.toHttp(response).sendRedirect(sso_server_url.toString());
        return false;
    }

    /**
     * 认证中心登录成功带回code
     * @param request
     */
    private boolean validateClient(ServletRequest request, ServletResponse response) {
        Subject subject = getSubject(request, response);
        Session session = subject.getSession();
        String sessionId = session.getId().toString();
        int timeOut = (int) session.getTimeout() / 1000;
        // 判断局部会话是否登录
        //String cacheClientSession = RedisUtil.get(TRA_MANAGER_CLIENT_SESSION_ID + "_" + session.getId());
        String cacheClientSession = (String)jedisClient.get(TRA_MANAGER_CLIENT_SESSION_ID + "_" + session.getId());
        if (StringUtils.isNotBlank(cacheClientSession)) {
            // 更新code有效期
            //RedisUtil.set(TRA_MANAGER_CLIENT_SESSION_ID + "_" + sessionId, cacheClientSession, timeOut);
            jedisClient.set(TRA_MANAGER_CLIENT_SESSION_ID + "_" + sessionId, cacheClientSession, timeOut);
            // Jedis jedis = RedisUtil.getJedis();
            // jedis.expire(TRA_MANAGER_CLIENT_SESSION_IDS + "_" + cacheClientSession, timeOut);
            //jedis.close();
            jedisClient.expire(TRA_MANAGER_CLIENT_SESSION_IDS + "_" + cacheClientSession, timeOut);
            // 移除url中的code参数
            if (null != request.getParameter("code")) {
                String backUrl = RequestParameterUtil.getParameterWithOutCode(WebUtils.toHttp(request));
                HttpServletResponse httpServletResponse = WebUtils.toHttp(response);
                try {
                    httpServletResponse.sendRedirect(backUrl.toString());
                } catch (IOException e) {
                    logger.error("局部会话已登录，移除code参数跳转出错：", e);
                }
            } else {
                return true;
            }
        }
        // 判断是否有认证中心code
        String code = request.getParameter("tra_code");
        // 已拿到code
        if (StringUtils.isNotBlank(code)) {
            // HttpPost去校验code
            try {
                StringBuffer sso_server_url = new StringBuffer(PropertiesFileUtil.getInstance("tra-manager-client").get("tra.manager.sso.server.url"));
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(sso_server_url.toString() + "/sso/code");

                List<NameValuePair> nameValuePairs = new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair("code", code));
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse httpResponse = httpclient.execute(httpPost);
                if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    HttpEntity httpEntity = httpResponse.getEntity();
                    JSONObject result = JSONObject.parseObject(EntityUtils.toString(httpEntity));
                    if (1 == result.getIntValue("code") && result.getString("data").equals(code)) {
                        // code校验正确，创建局部会话
                        //RedisUtil.set(TRA_MANAGER_CLIENT_SESSION_ID + "_" + sessionId, code, timeOut);
                        jedisClient.set(TRA_MANAGER_CLIENT_SESSION_ID + "_" + sessionId, code, timeOut);
                        // 保存code对应的局部会话sessionId，方便退出操作
                        //RedisUtil.sadd(TRA_MANAGER_CLIENT_SESSION_IDS + "_" + code, sessionId, timeOut);
                        jedisClient.sadd(TRA_MANAGER_CLIENT_SESSION_IDS + "_" + code, sessionId, timeOut);
                        logger.debug("当前code={}，对应的注册系统个数：{}个", code, RedisUtil.getJedis().scard(TRA_MANAGER_CLIENT_SESSION_IDS + "_" + code));
                        // 移除url中的token参数
                        String backUrl = RequestParameterUtil.getParameterWithOutCode(WebUtils.toHttp(request));
                        // 返回请求资源
                        try {
                            // client无密认证
                            String username = request.getParameter("tra_username");
                            subject.login(new UsernamePasswordToken(username, ""));
                            HttpServletResponse httpServletResponse = WebUtils.toHttp(response);
                            httpServletResponse.sendRedirect(backUrl.toString());
                            return true;
                        } catch (IOException e) {
                            logger.error("已拿到code，移除code参数跳转出错：", e);
                        }
                    } else {
                        logger.warn(result.getString("data"));
                    }
                }
            } catch (IOException e) {
                logger.error("验证token失败：", e);
            }
        }
        return false;
    }

}
