package eco.com.gurunanak.http

import android.os.AsyncTask
import android.os.StrictMode

import com.squareup.okhttp.Headers
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.squareup.okhttp.RequestBody
import com.squareup.okhttp.Response

import java.io.IOException

/**
 * Created by ss22493 on 17-06-2017.
 */
class OkHttpGetHeader(internal var strUrl: String, internal var mOkHttpListener: OkHttpListener?, internal var pageId: Int, internal var token: String) : AsyncTask<Void, Void, Void>() {
    internal var client = OkHttpClient()
    internal var formBody: RequestBody? = null
    internal lateinit var response: Response

    override fun doInBackground(vararg params: Void): Void? {
        //  OkHttpClient client = new OkHttpClient();
        //        client.networkInterceptors().add((Interceptor) new StethoInterceptor());

        val request = Request.Builder()
                .header("x-access-token", token)
                .url(strUrl)
                .addHeader("Content-Type", "application/json")
                .build()

        //  {method=GET, url=http://139.59.39.117:8080/api/pharma/inventory_in_vicinity?longitude=46.12345&latitude=23.1234&q=GSGSHS, tag=null}


        //RESPONSE: {"error_code":"atree-40012","error_message":"Failed to get inventory information.","success":false}
        //    Log.i("REQUEST", "" + request);

        try {
            //  Log.i("SENDING_DATA", "" + formBody.toString());
            //   android.util.Log.w("Test", formBody.toString());
            response = client.newCall(request).execute()
            //  if (!response.isSuccessful())
            //throw new IOException("unexpected code " + response.toString());
            //sendResponse(response.body().string(), pageId);
            //return response.body().string();
        } catch (e: Exception) {
            e.printStackTrace()
            // sendError("Server Error");
        }

        return null
    }

    override fun onPostExecute(aVoid: Void) {
        super.onPostExecute(aVoid)

        try {
            if (!response.isSuccessful)
                throw IOException("unexpected code " + response.toString())

            val responseHeaders = response.headers()
            for (i in 0 until responseHeaders.size()) {
                println(responseHeaders.name(i) + ": " + responseHeaders.value(i))
            }
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
            sendResponse(response.body().string(), pageId)
            //return response.body().string();
        } catch (e: Exception) {
            e.printStackTrace()
            sendError("Server Error")
        }

    }

    private fun sendResponse(response: String, pageId: Int) {
        if (mOkHttpListener != null) {
            mOkHttpListener!!.onOkHttpResponse(response, pageId)
        }
    }

    private fun sendError(error: String) {
        if (mOkHttpListener != null) {
            mOkHttpListener!!.onOkHttpError(error)
        }
    }
}
