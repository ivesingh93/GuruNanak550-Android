package eco.com.gurunanak

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log

class ActivitySplash : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        callIntent()
    }

    fun callIntent() {

        Log.e("test", "inside intent")
        Handler().postDelayed({

                val `in` = Intent(applicationContext, Login::class.java)
                startActivity(`in`)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                finish()
        }, 1500)
    }
}
