package com.javaxxw.shiro.listener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionListener;


/**
 * @desc 
 * @author Administrator
 * @since 2017/6/16
 * @version 1.0
 */
public class TraSessionListener implements SessionListener {

    private static Logger logger = LogManager.getLogger(TraSessionListener.class);

    @Override
    public void onStart(Session session) {
        logger.debug("会话创建：" + session.getId());
    }

    @Override
    public void onStop(Session session) {
        logger.debug("会话停止：" + session.getId());
    }

    @Override
    public void onExpiration(Session session) {
        logger.debug("会话过期：" + session.getId());
    }

}
