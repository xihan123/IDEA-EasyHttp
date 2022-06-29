package cn.xihan.lib.easyhttp.annotation;

import java.lang.annotation.*;

/**
 *    author : Android 轮子哥
 *    github : <a href="https://github.com/getActivity/EasyHttp">...</a>
 *    time   : 2019/05/19
 *    desc   : 忽略注解（这个参数或者请求头将不会被发送到后台）
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface HttpIgnore {}