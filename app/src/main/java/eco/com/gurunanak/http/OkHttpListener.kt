package eco.com.gurunanak.http

import com.google.android.gms.maps.model.LatLng

/**
 * Created by Admin on 1/4/2016.
 */
interface OkHttpListener {
    fun onOkHttpResponse(callResponse: String, pageId: Int)

    fun onOkHttpResponse(callResponse: String, pageId: Int, latLng: LatLng)
    fun onOkHttpError(error: String)


}
