package cn.xihan.lib.easyhttp.body

import cn.xihan.lib.easyhttp.EasyLog.printLog
import cn.xihan.lib.easyhttp.EasyUtils.getProgressProgress
import cn.xihan.lib.easyhttp.EasyUtils.post
import cn.xihan.lib.easyhttp.listener.OnUpdateListener
import cn.xihan.lib.easyhttp.request.HttpRequest
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.*
import java.io.IOException

/**
 * author : Android 轮子哥
 * github : [https://github.com/getActivity/EasyHttp](https://github.com/getActivity/EasyHttp)
 * time   : 2020/08/15
 * desc   : RequestBody 包装类（用于获取上传进度）
 */
class ProgressBody(
    private val mHttpRequest: HttpRequest<*>,
    private val mRequestBody: RequestBody,
    private val mListener: OnUpdateListener<*>?
) : RequestBody() {
    /** 总字节数  */
    private var mTotalByte: Long = 0

    /** 已上传字节数  */
    private var mUpdateByte: Long = 0

    /** 上传进度值  */
    private var mUpdateProgress = 0
    override fun contentType(): MediaType? {
        return mRequestBody.contentType()
    }

    @Throws(IOException::class)
    override fun contentLength(): Long {
        return mRequestBody.contentLength()
    }

    @Throws(IOException::class)
    override fun writeTo(sink: BufferedSink) {
        var sink = sink
        mTotalByte = contentLength()
        sink = WrapperSink(sink).buffer()
        mRequestBody.writeTo(sink)
        sink.flush()
    }

    private inner class WrapperSink(delegate: Sink?) : ForwardingSink(delegate!!) {
        @Throws(IOException::class)
        override fun write(source: Buffer, byteCount: Long) {
            super.write(source, byteCount)
            mUpdateByte += byteCount
            post {
                mListener?.onByte(mTotalByte, mUpdateByte)
                val progress = getProgressProgress(mTotalByte, mUpdateByte)
                // 只有上传进度发生改变的时候才回调此方法，避免引起不必要的 View 重绘
                if (progress != mUpdateProgress) {
                    mUpdateProgress = progress
                    mListener?.onProgress(progress)
                    printLog(
                        mHttpRequest, "Uploading in progress, uploaded: " +
                                mUpdateByte + " / " + mTotalByte +
                                ", progress: " + progress + "%"
                    )
                }
            }
        }
    }
}