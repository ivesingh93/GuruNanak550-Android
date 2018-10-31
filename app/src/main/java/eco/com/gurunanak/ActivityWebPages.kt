package eco.com.gurunanak



import android.graphics.Bitmap
import android.os.Bundle
import android.view.View

import com.tudle.utils.BaseActivity
import com.tudle.utils.DataModel
import android.webkit.*
import android.widget.*
import kotlinx.android.synthetic.main.activity_links.*


class ActivityWebPages : BaseActivity() {






    var webView:WebView?=null
    override protected fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_links)

        if(DataModel.Internetcheck(this)){

            init()
        }
        else{
            finish()
        }
    }

    private fun init() {

        webView = findViewById(R.id.webView) as WebView


        webView!!.getSettings().setJavaScriptEnabled(true)


        webView!!.webViewClient = object : WebViewClient() {


            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                progressBar.visibility= View.VISIBLE
            }


            override fun onPageFinished(view: WebView?, url: String?) {
                progressBar.visibility= View.GONE
                super.onPageFinished(view!!, url!!)
            }

        }



        webView!!.loadUrl(intent.getStringExtra("url"))
    }



}