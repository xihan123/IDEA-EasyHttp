package cn.xihan.lib.easyhttp.model

import cn.xihan.lib.easyhttp.EasyUtils.isEmpty
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import java.net.URLConnection

/**
 * author : Android 轮子哥
 * github : [https://github.com/getActivity/EasyHttp](https://github.com/getActivity/EasyHttp)
 * time   : 2021/03/31
 * desc   : RequestBody 包装类（用于获取上传进度）
 */
object ContentType {
    /** 字节流  */
    val STREAM: MediaType = "application/octet-stream".toMediaType()

    /** Json  */
    val JSON: MediaType = "application/json; charset=utf-8".toMediaType()

    /** 纯文本  */
    val TEXT: MediaType = "text/plain; charset=utf-8".toMediaType()

    /**
     * 根据文件名获取 MIME 类型
     */
    fun guessMimeType(fileName: String): MediaType {
        var fileName = fileName
        if (isEmpty(fileName)) {
            return STREAM
        }
        val fileNameMap = URLConnection.getFileNameMap()
        // 解决文件名中含有#号异常的问题
        fileName = fileName.replace("#", "")
        val contentType = fileNameMap.getContentTypeFor(fileName) ?: return STREAM
        return contentType.toMediaType()
    }
}