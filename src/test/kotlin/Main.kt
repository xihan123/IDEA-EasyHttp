import cn.xihan.lib.easyhttp.EasyConfig
import cn.xihan.lib.easyhttp.EasyHttp
import cn.xihan.lib.easyhttp.config.IRequestInterceptor
import cn.xihan.lib.easyhttp.listener.OnHttpListener
import cn.xihan.lib.easyhttp.model.HttpHeaders
import cn.xihan.lib.easyhttp.model.HttpParams
import cn.xihan.lib.easyhttp.model.RequestHandler
import cn.xihan.lib.easyhttp.request.HttpRequest
import okhttp3.OkHttpClient

fun main() {
    initEasyHttp()
    println("Hello, world!")
    HttpMethods()

}

fun HttpMethods() {
    Get()
}

fun Get() {
    EasyHttp.get()
        .api("/get")
        .request(object: OnHttpListener<String> {

            override fun onSucceed(result: String) {
                println("Get success: $result")
            }

            override fun onFail(e: Exception?) {
                println("Get error: ${e?.message}")
            }
        })
}

fun initEasyHttp() {
    EasyConfig.with(OkHttpClient())
        .setLogEnabled(false)
        .setServer("https://www.httpbin.org/")
        .setHandler(RequestHandler())
        .setInterceptor(object : IRequestInterceptor {
            override fun interceptArguments(
                httpRequest: HttpRequest<*>,
                params: HttpParams,
                headers: HttpHeaders,
            ) {
                headers.put("timestamp", System.currentTimeMillis().toString())
            }
        })
        .setRetryCount(2)
        .setRetryTime(2000)
        .addHeader("Connection", "close")
        .into()

}
