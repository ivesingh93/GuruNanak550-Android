package eco.com.gurunanak.utlity


import android.graphics.Bitmap
import android.os.Handler

import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer
import eco.com.gurunanak.R


object UIL_Image_options {


    var options = DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.my_logo)
            .showImageForEmptyUri(R.drawable.my_logo)
            .showImageOnFail(R.drawable.my_logo)
            .resetViewBeforeLoading(true)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(false)
            .bitmapConfig(Bitmap.Config.ARGB_8888)
            .displayer(SimpleBitmapDisplayer())
            .displayer(RoundedBitmapDisplayer(180))
            .handler(Handler())
            .build()







}
