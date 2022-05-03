package cn.xihan.lib.easyhttp.config

import cn.xihan.lib.easyhttp.EasyUtils.formatJson
import cn.xihan.lib.easyhttp.EasyUtils.isEmpty

/**
 * author : Android 轮子哥
 * github : [https://github.com/getActivity/EasyHttp](https://github.com/getActivity/EasyHttp)
 * time   : 2020/04/24
 * desc   : 网络请求日志打印默认实现
 */
class LogStrategy : ILogStrategy {
    override fun printLog(tag: String, log: String) {
        // 答应tag 和 log
        println("$tag----:$log")
    }

    override fun printJson(tag: String, json: String?) {
        var text = formatJson(json)
        if (isEmpty(text)) {
            return
        }

        // 打印 Json 数据最好换一行再打印会好看一点
        text = " \n$text"
        val segmentSize = 3 * 1024
        val length = text.length.toLong()
        if (length <= segmentSize) {
            // 长度小于等于限制直接打印
            printLog(tag, text)
            return
        }

        // 循环分段打印日志
        while (text.length > segmentSize) {
            val logContent = text.substring(0, segmentSize)
            text = text.replace(logContent, "")
            printLog(tag, logContent)
        }

        // 打印剩余日志
        printLog(tag, text)
    }

    override fun printKeyValue(tag: String, key: String?, value: String?) {
        printLog(tag, "$key = $value")
    }

    override fun printThrowable(tag: String, throwable: Throwable?) {
        println(tag + "----:" + throwable?.message)
    }

    override fun printStackTrace(tag: String, stackTrace: Array<StackTraceElement>) {
        for (element in stackTrace) {
            // 获取代码行数
            val lineNumber = element.lineNumber
            // 获取类的全路径
            val className = element.className
            if (lineNumber <= 0 || className.startsWith("cn.xihan.lib.easyhttp")) {
                continue
            }
            printLog(tag, "RequestCode = (" + element.fileName + ":" + lineNumber + ") ")
            break
        }
    }
}