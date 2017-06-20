package com.javaxxw.common.aspect;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author tuyong
 * @version 1.0
 * @DESCRIPTION 切换数据源(不同方法调用不同数据源)
 * @create 2017-06-15 11:14
 **/
@Aspect
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class DataSourceAspect {
    private final Logger logger = LogManager.getLogger(DataSourceAspect.class);

    /**
     * 配置切入点
     */
    @Pointcut("execution(* com.javaxxw.*.service..*.*(..))")
    public void aspect(){
    }

    /**
     *  配置前置通知,使用在方法aspect()上注册的切入点
     * @param point
     */
    @Before("aspect()")
    public void before(JoinPoint point){
        String className = point.getTarget().getClass().getName();
        String method = point.getSignature().getName();
        logger.info(className + "." + method + "(" + StringUtils.join(point.getArgs(), ",") + ")");
        try {
            L: for (String key : ChooseDataSource.METHODTYPE.keySet()) {
                for (String type : ChooseDataSource.METHODTYPE.get(key)) {
                    if (method.startsWith(type)) {
                        logger.info(key);
                        HandleDataSource.putDataSource(key);
                        break L;
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e);
            HandleDataSource.putDataSource("write");
        }

    }

    /**
     * 后置通知
     * @param point
     */
    @After("aspect()")
    public void after(JoinPoint point) {
        HandleDataSource.clear();
    }

}
