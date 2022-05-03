package cn.xihan.lib.easyhttp.exception

/**
 * author : Android 轮子哥
 * github : [https://github.com/getActivity/EasyHttp](https://github.com/getActivity/EasyHttp)
 * time   : 2019/05/19
 * desc   : 网络请求异常
 */
open class HttpException : Exception {
    /**
     * 获取错误信息
     */
    override val message: String
    private var mThrowable: Throwable? = null

    constructor(message: String) : super(message) {
        this.message = message
    }

    constructor(message: String, cause: Throwable?) : super(message, cause) {
        this.message = message
        mThrowable = cause
    }

    override fun getStackTrace(): Array<StackTraceElement> {
        return if (mThrowable != null) {
            mThrowable!!.stackTrace
        } else super.getStackTrace()
    }

    @get:Synchronized
    override val cause: Throwable
        get() = if (mThrowable != null) {
            mThrowable!!.cause!!
        } else super.cause!!
}