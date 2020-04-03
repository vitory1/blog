package com.cjc.blog.Aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Aspect
@Component
public class LogAspect {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Pointcut("execution(* com.cjc.blog.web.*.*(..))")
    public void log(){}

    @Before("log()")
    public void doBefore(JoinPoint joinPoint){
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String url = request.getRequestURL().toString();
        String ip = request.getRemoteAddr();
        Object[] args = joinPoint.getArgs();
        RequestLog requestLog = new RequestLog(url,ip,args);
        logger.info("RequestLog:{}",requestLog);
    }
    @After("log()")
    public void doAfter(){
        logger.info("---doAfter---");
    }
    @AfterReturning(returning = "result",pointcut = "log()")
    public void doAfterRuturn(Object result){
        logger.info("Result:{}",result);
    }

    private class RequestLog{
        private String url;
        private String ip;
        private Object[] args;

        public RequestLog(String url, String ip, Object[] args) {
            this.url = url;
            this.ip = ip;
            this.args = args;
        }

        @Override
        public String toString() {
            return "RequestLog{" +
                    "url='" + url + '\'' +
                    ", ip='" + ip + '\'' +
                    ", args=" + Arrays.toString(args) +
                    '}';
        }
    }
}
