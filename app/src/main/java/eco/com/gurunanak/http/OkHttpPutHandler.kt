package eco.com.gurunanak.http

import android.os.AsyncTask
import android.os.StrictMode
import android.util.Log

import com.squareup.okhttp.Headers
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.squareup.okhttp.RequestBody
import com.squareup.okhttp.Response

import java.io.IOException

/**
 * Created by ss22493 on 19-06-2017.
 */
class OkHttpPutHandler(internal var strUrl: String, internal var requestBody: RequestBody, internal var mOkHttpListener: OkHttpListener?, internal var pageId: Int, internal var token: String) : AsyncTask<Void, Void, Void>() {
    internal var client = OkHttpClient()
    internal var position: Int = 0
    internal lateinit var response: Response


    override fun doInBackground(vararg params: Void): Void? {

        /*   Request request = new Request.Builder()
                .url(strUrl)
                .put(formBody)
                .header("x-access-token", token)
                .build();*/


        val request = Request.Builder()
                .url(strUrl)
                .put(requestBody)
                .header("x-access-token", token)
                .build()

        Log.i("REQUEST", "" + request)

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
            sendResponse(response.body().string(), pageId, position)
            //return response.body().string();
        } catch (e: Exception) {
            e.printStackTrace()
            sendError("Server Error")
        }

    }

    private fun sendResponse(response: String, pageId: Int, position: Int) {
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
