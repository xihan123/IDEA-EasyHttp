package cn.xihan.lib.easyhttp.body

import cn.xihan.lib.easyhttp.EasyUtils.closeStream
import cn.xihan.lib.easyhttp.model.ContentType
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.BufferedSink
import okio.Source
import okio.source
import java.io.File
import java.io.IOException
import java.io.InputStream

/**
 * author : Android 轮子哥
 * github : [https://github.com/getActivity/EasyHttp](https://github.com/getActivity/EasyHttp)
 * time   : 2019/12/14
 * desc   : 上传文件流
 */
class UpdateBody(
    /** 上传源  */
    private val mSource: Source,
    /** 内容类型  */
    private val mMediaType: MediaType,
    /** 内容名称  */
    val keyName: String?,
    /** 内容大小  */
    private val mLength: Long
) : RequestBody() {

    constructor(file: File) : this(file.source(), ContentType.guessMimeType(file.name), file.name, file.length()) {}
    constructor(inputStream: InputStream, name: String?) : this(
        inputStream.source(),
        ContentType.STREAM,
        name,
        inputStream.available().toLong()
    ) {
    }

    override fun contentType(): MediaType? {
        return mMediaType
    }

    override fun contentLength(): Long {
        return if (mLength == 0L) {
            // 如果不能获取到大小，则返回 -1，参考 RequestBody.contentLength 方法实现
            -1
        } else mLength
    }

    @Throws(IOException::class)
    override fun writeTo(sink: BufferedSink) {
        try {
            sink.writeAll(mSource)
        } finally {
            closeStream(mSource)
        }
    }
}