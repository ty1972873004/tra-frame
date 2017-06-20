package com.javaxxw.shiro.session;

import com.javaxxw.common.constants.Constants;
import com.javaxxw.redis.service.RedisUtil;
import com.javaxxw.util.SerializableUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.ValidatingSession;
import org.apache.shiro.session.mgt.eis.CachingSessionDAO;
import redis.clients.jedis.Jedis;

import java.io.Serializable;
import java.util.*;


/**
 * @desc 基于redis的sessionDao，缓存共享session
 * @author tuyong
 * @since 2017/6/19
 * @version 1.0
 */
public class TraSessionDao extends CachingSessionDAO {

    private static Logger logger = LogManager.getLogger(TraSessionDao.class);
    // 会话key
    private final static String TRA_MANAGER_SHIRO_SESSION_ID = "tra-manager-shiro-session-id";
    // 全局会话key
    private final static String TRA_MANAGER_SERVER_SESSION_ID = "tra-manager-server-session-id";
    // 全局会话列表key
    private final static String TRA_MANAGER_SERVER_SESSION_IDS = "tra-manager-server-session-ids";
    // code key
    private final static String TRA_MANAGER_SERVER_CODE = "tra-manager-server-code";
    // 局部会话key
    private final static String TRA_MANAGER_CLIENT_SESSION_ID = "tra-manager-client-session-id";
    // 单点同一个code所有局部会话key
    private final static String TRA_MANAGER_CLIENT_SESSION_IDS = "tra-manager-client-session-ids";

    @Override
    protected Serializable doCreate(Session session) {
        Serializable sessionId = generateSessionId(session);
        assignSessionId(session, sessionId);
        RedisUtil.set(TRA_MANAGER_SHIRO_SESSION_ID + "_" + sessionId, SerializableUtil.serialize(session), (int) session.getTimeout() / 1000);
        logger.debug("doCreate >>>>> sessionId={}", sessionId);
        return sessionId;
    }

    @Override
    protected Session doReadSession(Serializable sessionId) {
        String session = RedisUtil.get(TRA_MANAGER_SHIRO_SESSION_ID + "_" + sessionId);
        logger.debug("doReadSession >>>>> sessionId={}", sessionId);
        return SerializableUtil.deserialize(session);
    }

    @Override
    protected void doUpdate(Session session) {
        // 如果会话过期/停止 没必要再更新了
        if(session instanceof ValidatingSession && !((ValidatingSession)session).isValid()) {
            return;
        }
        // 更新session的最后一次访问时间
        TraSession traSession = (TraSession) session;
        TraSession cacheTraSession = (TraSession) doReadSession(session.getId());
        if (null != cacheTraSession) {
            traSession.setStatus(cacheTraSession.getStatus());
            traSession.setAttribute("FORCE_LOGOUT", cacheTraSession.getAttribute("FORCE_LOGOUT"));
        }
        RedisUtil.set(TRA_MANAGER_SHIRO_SESSION_ID + "_" + session.getId(), SerializableUtil.serialize(session), (int) session.getTimeout() / 1000);
        // 更新TRA_MANAGER_SERVER_SESSION_ID、TRA_MANAGER_SERVER_CODE过期时间 TODO
        logger.debug("doUpdate >>>>> sessionId={}", session.getId());
    }

    @Override
    protected void doDelete(Session session) {
        String sessionId = session.getId().toString();
        String traType = ObjectUtils.toString(session.getAttribute(Constants.MANAGER_TYPE));
        if ("client".equals(traType)) {
            // 删除局部会话和同一code注册的局部会话
            String code = RedisUtil.get(TRA_MANAGER_CLIENT_SESSION_ID + "_" + sessionId);
            Jedis jedis = RedisUtil.getJedis();
            jedis.del(TRA_MANAGER_CLIENT_SESSION_ID + "_" + sessionId);
            jedis.srem(TRA_MANAGER_CLIENT_SESSION_IDS + "_" + code, sessionId);
            jedis.close();
        }
        if ("server".equals(traType)) {
            // 当前全局会话code
            String code = RedisUtil.get(TRA_MANAGER_SERVER_SESSION_ID + "_" + sessionId);
            // 清除全局会话
            RedisUtil.remove(TRA_MANAGER_SERVER_SESSION_ID + "_" + sessionId);
            // 清除code校验值
            RedisUtil.remove(TRA_MANAGER_SERVER_CODE + "_" + code);
            // 清除所有局部会话
            Jedis jedis = RedisUtil.getJedis();
            Set<String> clientSessionIds = jedis.smembers(TRA_MANAGER_CLIENT_SESSION_IDS + "_" + code);
            for (String clientSessionId : clientSessionIds) {
                jedis.del(TRA_MANAGER_CLIENT_SESSION_ID + "_" + clientSessionId);
                jedis.srem(TRA_MANAGER_CLIENT_SESSION_IDS + "_" + code, clientSessionId);
            }
            logger.debug("当前code={}，对应的注册系统个数：{}个", code, jedis.scard(TRA_MANAGER_CLIENT_SESSION_IDS + "_" + code));
            jedis.close();
            // 维护会话id列表，提供会话分页管理
            RedisUtil.lrem(TRA_MANAGER_SERVER_SESSION_IDS, 1, sessionId);
        }
        // 删除session
        RedisUtil.remove(TRA_MANAGER_SHIRO_SESSION_ID + "_" + sessionId);
        logger.debug("doUpdate >>>>> sessionId={}", sessionId);
    }

    /**
     * 获取会话列表
     * @param offset
     * @param limit
     * @return
     */
    public Map getActiveSessions(int offset, int limit) {
        Map sessions = new HashMap();
        Jedis jedis = RedisUtil.getJedis();
        // 获取在线会话总数
        long total = jedis.llen(TRA_MANAGER_SERVER_SESSION_IDS);
        // 获取当前页会话详情
        List<String> ids = jedis.lrange(TRA_MANAGER_SERVER_SESSION_IDS, offset, (offset + limit - 1));
        List<Session> rows = new ArrayList<>();
        for (String id : ids) {
            String session = RedisUtil.get(TRA_MANAGER_SHIRO_SESSION_ID + "_" + id);
            // 过滤redis过期session
            if (null == session) {
                RedisUtil.lrem(TRA_MANAGER_SERVER_SESSION_IDS, 1, id);
                total = total - 1;
                continue;
            }
             rows.add(SerializableUtil.deserialize(session));
        }
        jedis.close();
        sessions.put("total", total);
        sessions.put("rows", rows);
        return sessions;
    }

    /**
     * 强制退出
     * @param ids
     * @return
     */
    public int forceout(String ids) {
        String[] sessionIds = ids.split(",");
        for (String sessionId : sessionIds) {
            // 会话增加强制退出属性标识，当此会话访问系统时，判断有该标识，则退出登录
            String session = RedisUtil.get(TRA_MANAGER_SHIRO_SESSION_ID + "_" + sessionId);
            TraSession traSession = (TraSession) SerializableUtil.deserialize(session);
            traSession.setStatus(TraSession.OnlineStatus.force_logout);
            traSession.setAttribute("FORCE_LOGOUT", "FORCE_LOGOUT");
            RedisUtil.set(TRA_MANAGER_SHIRO_SESSION_ID + "_" + sessionId, SerializableUtil.serialize(traSession), (int) traSession.getTimeout() / 1000);
        }
        return sessionIds.length;
    }

    /**
     * 更改在线状态
     *
     * @param sessionId
     * @param onlineStatus
     */
    public void updateStatus(Serializable sessionId, TraSession.OnlineStatus onlineStatus) {
        TraSession session = (TraSession) doReadSession(sessionId);
        if (null == session) {
            return;
        }
        session.setStatus(onlineStatus);
        RedisUtil.set(TRA_MANAGER_SHIRO_SESSION_ID + "_" + session.getId(), SerializableUtil.serialize(session), (int) session.getTimeout() / 1000);
    }

}
