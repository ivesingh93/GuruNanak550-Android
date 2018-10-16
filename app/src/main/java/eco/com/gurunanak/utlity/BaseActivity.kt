package com.tudle.utils


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import eco.com.gurunanak.R


open class BaseActivity : AppCompatActivity() {

    var isActive: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = this.resources.getColor(R.color.header)
        }


    }

    fun BackBtn(v: View) {


        setResult(Activity.RESULT_OK, Intent().putExtra("counter", 109))

        finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        //  finish();

    }

    public override fun onResume() {

        window.setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        //        DataModel.ChangeStatusBarColor(this);
        super.onResume()

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        try {
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        } catch (e: Exception) {
        }

        return true
    }

}