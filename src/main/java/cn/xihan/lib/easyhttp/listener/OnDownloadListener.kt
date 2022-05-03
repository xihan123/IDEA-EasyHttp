package cn.xihan.lib.easyhttp.listener

import java.io.File

/**
 * author : Android 轮子哥
 * github : [https://github.com/getActivity/EasyHttp](https://github.com/getActivity/EasyHttp)
 * time   : 2019/05/19
 * desc   : 下载监听器
 */
interface OnDownloadListener {
    /**
     * 下载开始
     */
    fun onStart(file: File?)

    /**
     * 下载字节改变
     *
     * @param totalByte             总字节数
     * @param downloadByte          已下载字节数
     */
    fun onByte(file: File?, totalByte: Long, downloadByte: Long) {}

    /**
     * 下载进度改变
     *
     * @param progress              下载进度值（0-100）
     */
    fun onProgress(file: File?, progress: Int)

    /**
     * 请求成功
     *
     * @param cache         是否是通过缓存下载成功的
     */
    fun onComplete(file: File?, cache: Boolean) {
        onComplete(file)
    }

    /**
     * 下载完成
     */
    fun onComplete(file: File?)

    /**
     * 下载出错
     */
    fun onError(file: File?, e: Exception?)

    /**
     * 下载结束
     */
    fun onEnd(file: File?)
}