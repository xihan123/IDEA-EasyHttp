package cn.xihan.lib.easyhttp.model

import cn.xihan.lib.easyhttp.EasyLog.printJson
import cn.xihan.lib.easyhttp.EasyUtils
import cn.xihan.lib.easyhttp.EasyUtils.isNetworkAvailable
import cn.xihan.lib.easyhttp.config.IRequestHandler
import cn.xihan.lib.easyhttp.exception.*
import cn.xihan.lib.easyhttp.request.HttpRequest
import cn.xihan.lib.gson.factory.GsonFactory
import com.google.gson.JsonSyntaxException
import okhttp3.Headers
import okhttp3.Response
import okhttp3.ResponseBody
import java.io.IOException
import java.io.InputStream
import java.lang.reflect.GenericArrayType
import java.lang.reflect.Type
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * author : Android 轮子哥
 * github : [https://github.com/getActivity/EasyHttp](https://github.com/getActivity/EasyHttp)
 * time   : 2019/05/19
 * desc   : 请求处理类
 */
class RequestHandler : IRequestHandler {


    @Throws(Exception::class)
    override fun requestSucceed(httpRequest: HttpRequest<*>?, response: Response?, type: Type?): Any? {
        if (Response::class.java == type) {
            return response
        }
        if (!response!!.isSuccessful) {
            // 返回响应异常
            throw ResponseException(
                EasyUtils.RESPONSE_ERROR + ", responseCode: " + response.code + ", message: " + response.message,
                response
            )
        }
        if (Headers::class.java == type) {
            return response.headers
        }
        val body = response.body ?: throw NullBodyException(EasyUtils.DATA_ERROR)
        if (ResponseBody::class.java == type) {
            return body
        }

        // 如果是用数组接收，判断一下是不是用 byte[] 类型进行接收的
        if (type is GenericArrayType) {
            val genericComponentType = type.genericComponentType
            if (Byte::class.javaPrimitiveType == genericComponentType) {
                return body.bytes()
            }
        }
        if (InputStream::class.java == type) {
            return body.byteStream()
        }
        val text: String
        text = try {
            body.string()
        } catch (e: IOException) {
            // 返回结果读取异常
            throw DataException(EasyUtils.RESPONSE_ERROR, e)
        }

        // 打印这个 Json 或者文本
        printJson(httpRequest, text)
        if (String::class.java == type) {
            return text
        }
        val result: Any
        result = try {
            GsonFactory.getSingletonGson().fromJson<Any>(text, type)
        } catch (e: JsonSyntaxException) {
            // 返回结果读取异常
            throw DataException(EasyUtils.DATA_ERROR, e)
        }
        if (result is HttpData<*>) {
            val model = result
            if (model.isRequestSucceed) {
                // 代表执行成功
                return result
            }
            if (model.isTokenFailure) {
                // 代表登录失效，需要重新登录
                throw TokenException(EasyUtils.LOGIN_INVALID)
            }
            throw ResultException(model.message, model)
        }
        return result
    }

    override fun requestFail(httpRequest: HttpRequest<*>?, e: Exception?): Exception? {
        if (e is HttpException) {
            if (e is TokenException) {
                // 登录信息失效，跳转到登录页
            }
            return e
        }
        if (e is SocketTimeoutException) {
            return TimeoutException(EasyUtils.TIME_OUT, e)
        }
        if (e is UnknownHostException) {

            // 判断网络是否连接
            return if (isNetworkAvailable) {
                // 有连接就是服务器的问题
                ServerException(EasyUtils.CONNECT_ERROR, e)
            } else NetworkException(EasyUtils.ERROR, e)
            // 没有连接就是网络异常
        }
        return if (e is IOException) {
            CancelException(EasyUtils.REQUEST_CANCEL, e)
        } else HttpException(e!!.message!!, e)
    }
}