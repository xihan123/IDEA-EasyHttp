package cn.xihan.lib.easyhttp.config

/**
 * author : Android 轮子哥
 * github : [https://github.com/getActivity/EasyHttp](https://github.com/getActivity/EasyHttp)
 * time   : 2020/04/24
 * desc   : 日志打印策略
 */
interface ILogStrategy {
    /**
     * 打印分割线
     */
    fun printLine(tag: String) {
        printLog(tag, "----------------------------------------")
    }

    /**
     * 打印日志
     */
    fun printLog(tag: String, log: String)

    /**
     * 打印 Json
     */
    fun printJson(tag: String, json: String?)

    /**
     * 打印键值对
     */
    fun printKeyValue(tag: String, key: String?, value: String?)

    /**
     * 打印异常
     */
    fun printThrowable(tag: String, throwable: Throwable?)


    /**
     * 打印堆栈
     */
    fun printStackTrace(tag: String, stackTrace: Array<StackTraceElement>)
}