package eco.com.gurunanak.http

/**
 * Created by Admin on 1/4/2016.
 */
interface OkHttpListener {
    fun onOkHttpResponse(callResponse: String, pageId: Int)

    fun onOkHttpError(error: String)


}
