package cn.xihan.lib.easyhttp.annotation

/**
 * author : Android 轮子哥
 * github : [https://github.com/getActivity/EasyHttp](https://github.com/getActivity/EasyHttp)
 * time   : 2019/05/19
 * desc   : 重命名注解
 */
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class HttpRename(
    /**
     * 默认以字段的名称作为参数名，使用此注解可修改
     */
    val value: String
)