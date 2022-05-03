package cn.xihan.lib.easyhttp.callback


import cn.xihan.lib.easyhttp.EasyLog.printLog
import cn.xihan.lib.easyhttp.EasyLog.printThrowable
import cn.xihan.lib.easyhttp.EasyUtils.closeStream
import cn.xihan.lib.easyhttp.EasyUtils.createFolder
import cn.xihan.lib.easyhttp.EasyUtils.getFileMd5
import cn.xihan.lib.easyhttp.EasyUtils.getProgressProgress
import cn.xihan.lib.easyhttp.EasyUtils.isEmpty
import cn.xihan.lib.easyhttp.EasyUtils.openFileInputStream
import cn.xihan.lib.easyhttp.EasyUtils.openFileOutputStream
import cn.xihan.lib.easyhttp.EasyUtils.post
import cn.xihan.lib.easyhttp.exception.MD5Exception
import cn.xihan.lib.easyhttp.exception.NullBodyException
import cn.xihan.lib.easyhttp.listener.OnDownloadListener
import cn.xihan.lib.easyhttp.request.HttpRequest
import okhttp3.Call
import okhttp3.Response
import java.io.File

/**
 * author : Android 轮子哥
 * github : [https://github.com/getActivity/EasyHttp](https://github.com/getActivity/EasyHttp)
 * time   : 2019/11/25
 * desc   : 下载接口回调
 */
class DownloadCallback(
    /** 请求配置  */
    private val mHttpRequest: HttpRequest<*>
) : BaseCallback(mHttpRequest) {
    /** 保存的文件  */
    private var mFile: File? = null

    /** 校验的 MD5  */
    private var mMd5: String? = null

    /** 下载监听回调  */
    private var mListener: OnDownloadListener? = null

    /** 下载总字节  */
    private var mTotalByte: Long = 0

    /** 已下载字节  */
    private var mDownloadByte: Long = 0

    /** 下载进度  */
    private var mDownloadProgress = 0
    fun setFile(file: File?): DownloadCallback {
        mFile = file
        return this
    }

    fun setMd5(md5: String?): DownloadCallback {
        mMd5 = md5
        return this
    }

    fun setListener(listener: OnDownloadListener?): DownloadCallback {
        mListener = listener
        return this
    }

    override fun onStart(call: Call?) {
        post {
            if (mListener == null) {
                return@post
            }
            mListener!!.onStart(mFile)
        }
    }

    @Throws(Exception::class)
    override fun onResponse(response: Response?) {
        // 打印请求耗时时间
        var response = response
        printLog(
            mHttpRequest, "RequestConsuming：" +
                    (response!!.receivedResponseAtMillis - response.sentRequestAtMillis) + " ms"
        )
        val interceptor = mHttpRequest.requestInterceptor
        if (interceptor != null) {
            response = interceptor.interceptResponse(mHttpRequest, response)
        }

        // 如果没有指定文件的 md5 值
        if (mMd5 == null) {
            // 获取响应头中的文件 MD5 值
            val md5 = response!!.header("Content-MD5")
            // 这个 md5 值必须是文件的 md5 值
            if (!isEmpty(md5) && md5!!.matches(Regex(FILE_MD5_REGEX))) {
                mMd5 = md5
            }
        }
        val parentFile = mFile!!.parentFile
        if (parentFile != null) {
            createFolder(parentFile)
        }
        val body = response!!.body ?: throw NullBodyException("The response body is empty")
        mTotalByte = body.contentLength()
        if (mTotalByte < 0) {
            mTotalByte = 0
        }

        // 如果这个文件已经下载过，并且经过校验 MD5 是同一个文件的话，就直接回调下载成功监听
        if (!isEmpty(mMd5) && mFile!!.isFile &&
            mMd5.equals(getFileMd5(openFileInputStream(mFile!!)), ignoreCase = true)
        ) {
            post {
                if (mListener == null) {
                    return@post
                }
                mListener!!.onComplete(mFile, true)
                mListener!!.onEnd(mFile)
                // 文件已存在，跳过下载
                printLog(mHttpRequest, mFile!!.path + " file already exists, skip download")
            }
            return
        }
        var readLength: Int
        mDownloadByte = 0
        val bytes = ByteArray(8192)
        val inputStream = body.byteStream()
        val outputStream = openFileOutputStream(mFile!!)
        while (inputStream.read(bytes).also { readLength = it } != -1) {
            mDownloadByte += readLength.toLong()
            outputStream.write(bytes, 0, readLength)
            post {
                if (mListener == null) {
                    return@post
                }
                mListener!!.onByte(mFile, mTotalByte, mDownloadByte)
                val progress = getProgressProgress(mTotalByte, mDownloadByte)
                // 只有下载进度发生改变的时候才回调此方法，避免引起不必要的 View 重绘
                if (progress != mDownloadProgress) {
                    mDownloadProgress = progress
                    mListener!!.onProgress(mFile, mDownloadProgress)
                    printLog(
                        mHttpRequest, mFile!!.path +
                                ", downloaded: " + mDownloadByte + " / " + mTotalByte +
                                ", progress: " + progress + " %"
                    )
                }
            }
        }
        closeStream(inputStream)
        closeStream(outputStream)
        val md5 = getFileMd5(openFileInputStream(mFile!!))
        if (!isEmpty(mMd5) && !mMd5.equals(md5, ignoreCase = true)) {
            // 文件 MD5 值校验失败
            throw MD5Exception("MD5 verify failure", md5!!)
        }
        post {
            if (mListener == null) {
                return@post
            }
            mListener!!.onComplete(mFile, false)
            mListener!!.onEnd(mFile)
        }
    }

    override fun onFailure(e: Exception) {
        printThrowable(mHttpRequest, e)
        // 打印错误堆栈
        val finalException = mHttpRequest.requestHandler!!.requestFail(mHttpRequest, e)
        if (finalException !== e) {
            printThrowable(mHttpRequest, finalException)
        }
        post {
            if (mListener == null) {
                return@post
            }
            mListener!!.onError(mFile, finalException)
            mListener!!.onEnd(mFile)
            printLog(mHttpRequest, mFile!!.path + " download completed")
        }
    }

    companion object {
        /** 文件 MD5 正则表达式  */
        private const val FILE_MD5_REGEX = "^[\\w]{32}$"
    }
}