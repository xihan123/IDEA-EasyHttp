package cn.xihan.http.config;


import cn.xihan.http.EasyUtils;


/**
 *    author : Android 轮子哥
 *    github : <a href="https://github.com/getActivity/EasyHttp">https://github.com/getActivity/EasyHttp</a>
 *    time   : 2020/04/24
 *    desc   : 网络请求日志打印默认实现
 */
public final class LogStrategy implements ILogStrategy {

    @Override
    public void printLog(String tag, String log) {
        // 答应tag 和 log

        System.out.println(tag + "----:" + log);
    }

    @Override
    public void printJson(String tag, String json) {
        String text = EasyUtils.formatJson(json);
        if (EasyUtils.isEmpty(text)) {
            return;
        }

        // 打印 Json 数据最好换一行再打印会好看一点
        text = " \n" + text;

        int segmentSize = 3 * 1024;
        long length = text.length();
        if (length <= segmentSize) {
            // 长度小于等于限制直接打印
            printLog(tag, text);
            return;
        }

        // 循环分段打印日志
        while (text.length() > segmentSize) {
            String logContent = text.substring(0, segmentSize);
            text = text.replace(logContent, "");
            printLog(tag, logContent);
        }

        // 打印剩余日志
        printLog(tag, text);
    }

    @Override
    public void printKeyValue(String tag, String key, String value) {
        printLog(tag, key + " = " + value);
    }

    @Override
    public void printThrowable(String tag, Throwable throwable) {
        System.out.println(tag + "----:" +  throwable.getMessage());
    }

    @Override
    public void printStackTrace(String tag, StackTraceElement[] stackTrace) {
        for (StackTraceElement element : stackTrace) {
            // 获取代码行数
            int lineNumber = element.getLineNumber();
            // 获取类的全路径
            String className = element.getClassName();
            if (lineNumber <= 0 || className.startsWith("com.hjq.http")) {
                continue;
            }

            printLog(tag, "RequestCode = (" + element.getFileName() + ":" + lineNumber + ") ");
            break;
        }
    }
}