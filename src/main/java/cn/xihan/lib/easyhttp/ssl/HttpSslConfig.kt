package cn.xihan.lib.easyhttp.ssl

import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

/**
 * author : Android 轮子哥
 * github : [https://github.com/getActivity/EasyHttp](https://github.com/getActivity/EasyHttp)
 * time   : 2020/11/30
 * desc   : Https 配置类
 */
class HttpSslConfig internal constructor(val sslSocketFactory: SSLSocketFactory, val trustManager: X509TrustManager?)