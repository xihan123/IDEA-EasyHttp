package cn.xihan.lib.easyhttp.annotation;

import java.lang.annotation.*;

/**
 *    author : Android 轮子哥
 *    github : <a href="https://github.com/getActivity/EasyHttp">...</a>
 *    time   : 2019/05/19
 *    desc   : 请求头注解（标记这个字段是一个请求头的参数）
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface HttpHeader {}