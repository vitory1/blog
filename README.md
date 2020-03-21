

1. Command+P ：提示所要输入的参数
2. Command+Shift+n ：查找文件
3. Command+option+v
4. Command+e :切换最近使用的窗口



## 1git初始化本地仓库（Mac）

1. 找到要上传的代码，git init

2. git add . (将文件加载到缓存区)

3. git commit -m “init repo”

4. vim .git/config 进入config文件中加入name和邮箱名

   [user]

     			name = XXX

   ​			  address = XXX

5. ```
   git remote add origin git@github.com:vitory1/Spring_boot_cjc.git
   git push -u origin master 
   ```

6. git commit --amend --no-edit(追加刚刚更改的内容，不需要重新备注)
##2、框架搭建

### 2.1、构建与配置

- **引入spring boot 模块**

  - web

  - Thymeleaf

  - JPA

  - MySQL

  - Aspect

  - DevTools

- **application.yml配置**

  1. application.yml:

     ```yaml
     spring:
       thymeleaf:
         mode: HTML
     ```

     *  数据库连接配置

     ```yaml
     spring:
       datasource:
         driver-class-name: com.mysql.jdbc.Driver
         url: jdbc:mysql://localhost:3306/blog?useUnicode=true&characterEncoding=utf-8
         username: root
         password: root
       jpa:
         hibernate:
           ddl-auto: update
         show-sql: true
     ```

     * 日志配置

       application.yml:

     ```yaml
     logging:
       level:
         root: info
         com.imcoding: debug
       file: 
       		log/imcoding.log
     ```

     ​	logback-spring.xml：

     ```xml
     <?xml version="1.0" encoding="UTF-8" ?>
     <configuration>
         <!--包含Spring boot对logback日志的默认配置-->
         <include resource="org/springframework/boot/logging/logback/defaults.xml" />
         <property name="LOG_FILE" value="${LOG_FILE:-${LOG_PATH:-${LOG_TEMP:-${java.io.tmpdir:-/tmp}}}/spring.log}"/>
         <include resource="org/springframework/boot/logging/logback/console-appender.xml" />
     
         <!--重写了Spring Boot框架 org/springframework/boot/logging/logback/file-appender.xml 配置-->
         <appender name="TIME_FILE"
                   class="ch.qos.logback.core.rolling.RollingFileAppender">
             <encoder>
                 <pattern>${FILE_LOG_PATTERN}</pattern>
             </encoder>
             <file>${LOG_FILE}</file>
             <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
                 <fileNamePattern>${LOG_FILE}.%d{yyyy-MM-dd}.%i</fileNamePattern>
                 <!--保留历史日志一个月的时间-->
                 <maxHistory>30</maxHistory>
                 <!--
                 Spring Boot默认情况下，日志文件10M时，会切分日志文件,这样设置日志文件会在100M时切分日志
                 -->
                 <timeBasedFileNamingAndTriggeringPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                     <maxFileSize>10MB</maxFileSize>
                 </timeBasedFileNamingAndTriggeringPolicy>
     
             </rollingPolicy>
         </appender>
     
         <root level="INFO">
             <appender-ref ref="CONSOLE" />
             <appender-ref ref="TIME_FILE" />
         </root>
     
     </configuration>
     <!--
         1、继承Spring boot logback设置（可以在appliaction.yml或者application.properties设置logging.*属性）
         2、重写了默认配置，设置日志文件大小在100MB时，按日期切分日志，切分后目录：
     
             my.2017-08-01.0   80MB
             my.2017-08-01.1   10MB
             my.2017-08-02.0   56MB
             my.2017-08-03.0   53MB
             ......
     -->
     ```

     * 生产环境与开发环境配置

       * application-dev.yml

       * application-pro.yml

## 3、异常处理

###3.1、对于错误页面的测试

首先在template下见一个error文件夹，里面放404和500的错误页

![](/Users/cjc/Downloads/截图/Xnip2020-03-21_16-33-45.jpg)

###3.2、自定义错误页面

1. 不用springboot默认给你的拦截器。自定义拦截器，使得当页面报错之后，可以自动跳转到自己定义的错误页面，而不是500页面。

新建一个包 handler --->ControllerExceptionHandler 在其中编写实现拦截器功能的代码。

```java
package com.cjc.blog.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ControllerExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @ExceptionHandler(Exception.class)
    public ModelAndView exceptionHandler(HttpServletRequest request,Exception e){
        logger.error("url: {}, Exception: {}",request.getRequestURL(),e.getMessage());
       /**
         * if语句表明了通过注解定义的异常类型不等于null时，可以将这个异常抛出，并不用自己编写的拦截器将这个					异常拦截，交给springboot处理
         */
      if (AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class)!=null){
            throw e;
        }
        ModelAndView mv = new ModelAndView();
        mv.addObject("url",request.getRequestURL());
        mv.addObject("exception",e);
        mv.setViewName("error/error");
        return mv;
    }
}
```

2. 还可以在错误页面中添加错误信息：

```html
<div>
    <div th:utext="'&lt;!--'" th:remove="tag"></div>
    <div th:utext="'Failed Request URL : ' + ${url}" th:remove="tag"></div>
    <div th:utext="'Exception message : ' + ${exception.message}" th:remove="tag"></div>
    <ul th:remove="tag">
        <li th:each="st : ${exception.stackTrace}" th:remove="tag"><span th:utext="${st}" th:remove="tag"></span></li>
    </ul>
    <div th:utext="'--&gt;'" th:remove="tag"></div>
</div>
```

3. 当页面资源找不到时，我要设置它自动跳转到404页面，但是之前已经写好了错误跳转页面，使得springboot自动跳转失效，所以必须要在自动跳转页面加个if语句，将情况写明。

   自定义NOTFUOUND

   ```java
   package com.cjc.blog.web;
   
   import org.springframework.http.HttpStatus;
   import org.springframework.web.bind.annotation.ResponseStatus;
   
   @ResponseStatus(HttpStatus.NOT_FOUND)
   public class NotFoundException extends RuntimeException {
       public NotFoundException() {
       }
   
       public NotFoundException(String message) {
           super(message);
       }
   
       public NotFoundException(String message, Throwable cause) {
           super(message, cause);
       }
   }
   
   ```

   