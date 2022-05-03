package cn.xihan.lib.easyhttp.model

/**
 * author : Android 轮子哥
 * github : [https://github.com/getActivity/EasyHttp](https://github.com/getActivity/EasyHttp)
 * time   : 2019/05/19
 * desc   : 统一接口数据结构
 */
open class HttpData<T> {
    /** 返回码  */
    val code = 0

    /** 提示语  */
    val message: String = ""

    /** 数据  */
    val data: T? = null

    /**
     * 是否请求成功
     */
    val isRequestSucceed: Boolean
        get() = this.code == 0

    /**
     * 是否 Token 失效
     */
    val isTokenFailure: Boolean
        get() = this.code == 1001
}