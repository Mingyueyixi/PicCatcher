package com.pic.catcher.util.http

import java.io.Closeable
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

//懒得引入各种库，直接基于HttpConnection封装一个简易http工具
class HttpConnectUtil {

    open class Response(
        var code: Int,
        var header: Map<String, List<String>>,
        private var _body: ByteArray,
        var bodyStream: InputStream? = null,
        var connection: HttpURLConnection?,
        var error: Throwable? = null
    ) : Closeable{
        var body: ByteArray
            get() {
                if (_body.isEmpty()) {
                    return bodyStream?.readBytes() ?: _body
                }
                return _body
            }
            set(value) {
                _body = value
            }

        override fun toString(): String {
            return "Response(code=$code, header=$header, body=${body.toString(Charsets.UTF_8)}, error=$error)"
        }

        override open fun close() {
            connection?.disconnect()
            bodyStream?.close()
        }
    }

    class Request(
        var url: String,
        var method: String,
        //虽然http协议允许重复的 header，但实际没什么用，所以这里不许
        var header: Map<String, String>? = mutableMapOf(),
        var body: ByteArray? = null,
        var connectTimeOut: Int = 10000,
        var readTimeOut: Int = 10000,
        var isStreamMode: Boolean = false,
    ) {
        override fun toString(): String {
            return "Request(url='$url', method='$method', header=$header, body=${body?.toString(Charsets.UTF_8)}, connectTimeOut=$connectTimeOut, readTimeOut=$readTimeOut)"
        }
    }

    class Fetcher(var request: Request) {

        fun fetch(): Response {
            var connection: HttpURLConnection? = null
            var iStream: InputStream? = null
            var outStream: OutputStream? = null
            val isStreamMode = request.isStreamMode

            val resp = Response(-1, mutableMapOf(), byteArrayOf(), null, null)
            try {
                connection = URL(request.url).openConnection() as HttpURLConnection
                resp.connection = connection
                connection.requestMethod = request.method
                connection.connectTimeout = request.connectTimeOut
                connection.readTimeout = request.readTimeOut
                request.header?.forEach {
                    connection.setRequestProperty(it.key, it.value)
                }
                request.body?.let {
                    connection.doOutput = true
                    val out = connection.outputStream
                    outStream = out
                    out.write(it)
                    out.flush()
                }
                resp.code = connection.responseCode
                resp.header = connection.headerFields
                if (isStreamMode) {
                    resp.bodyStream = connection.inputStream
                } else {
                    iStream = connection.getInputStream()
                    resp.body = iStream.readBytes()
                }
            } catch (e: Throwable) {
                resp.error = e
            } finally {
                if (!isStreamMode) {
                    try {
                        connection?.disconnect()
                        outStream?.close()
                        iStream?.close()
                    } catch (e: Exception) {
                        resp.error = e
                    }
                }
            }
            return resp
        }
    }

    companion object {
        @JvmField
        val httpExecutor = ThreadPoolExecutor(8, 16, 0L, TimeUnit.MILLISECONDS, LinkedBlockingQueue())

        @JvmStatic
        val noCacheHttpHeader = mapOf("Cache-Control" to "no-cache")

        @JvmStatic
        fun get(url: String, callback: (resp: Response) -> Unit) {
            httpExecutor.submit {
                Fetcher(Request(url, "GET")).fetch().let(callback)
            }
        }

        @JvmStatic
        fun get(url: String, header: Map<String, String>, callback: (resp: Response) -> Unit) {
            httpExecutor.submit {
                Fetcher(Request(url, "GET", header)).fetch().let(callback)
            }
        }

        @JvmStatic
        fun request(
            method: String,
            url: String,
            header: Map<String, String>? = null,
            body: ByteArray? = null,
            isStreamMode: Boolean = false,
            callback: (resp: Response) -> Unit
        ) {
            httpExecutor.submit {
                Fetcher(Request(url, method, header, isStreamMode = isStreamMode, body = body)).fetch().let(callback)
            }
        }

        @JvmStatic
        fun getWithRetry(
            url: String,
            header: Map<String, String>,
            retryCount: Int,
            onEachFetch: (retryCount: Int, res: Response) -> Unit,
            onFinalCallback: (resp: Response) -> Unit
        ) {
            httpExecutor.submit {
                val fetcher = Fetcher(Request(url, "GET", header))
                var res = fetcher.fetch()
                onEachFetch(0, res)
                if (res.error != null) {
                    for (i in 0 until retryCount) {
                        res = fetcher.fetch()
                        onEachFetch(i + 1, res)
                        if (res.error == null) {
                            break
                        }
                    }
                }
                onFinalCallback.invoke(res)
            }
        }

        @JvmStatic
        fun postJson(url: String, json: String, callback: (resp: Response) -> Unit) {
            httpExecutor.submit {
                Fetcher(
                    Request(
                        url,
                        "POST",
                        mutableMapOf("Content-Type" to "application/json; charset=utf-8"),
                        json.toByteArray(Charsets.UTF_8)
                    )
                ).fetch().let(callback)
            }
        }
    }
}