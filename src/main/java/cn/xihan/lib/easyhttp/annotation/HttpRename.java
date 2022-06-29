package cn.xihan.lib.easyhttp.annotation;

import java.lang.annotation.*;

/**
 *    author : Android 轮子哥
 *    github : <a href="https://github.com/getActivity/EasyHttp">...</a>
 *    time   : 2019/05/19
 *    desc   : 重命名注解
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface HttpRename {

    /**
     * 默认以字段的名称作为参数名，使用此注解可修改
     */
    String value();
}