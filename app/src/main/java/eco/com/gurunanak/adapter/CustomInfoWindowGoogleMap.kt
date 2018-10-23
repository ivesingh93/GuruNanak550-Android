package eco.com.gurunanak.adapter

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import eco.com.gurunanak.R
import eco.com.gurunanak.model.InfoWindowData
import eco.com.gurunanak.model.JBJBMarker
import kotlinx.android.synthetic.main.map_custom_infowindow.*

class CustomInfoWindowGoogleMap(private val context: Context) : GoogleMap.InfoWindowAdapter {

    override fun getInfoWindow(marker: Marker): View? {
        return null
    }

    override fun getInfoContents(marker: Marker): View {
        val view = (context as Activity).layoutInflater
                .inflate(R.layout.map_custom_infowindow, null)


        val name_tv = view.findViewById<View>(R.id.name) as TextView

        val details_tv = view.findViewById<View>(R.id.details) as TextView

        //val img = view.findViewById(R.id.pic)

        val hotel_tv = view.findViewById<View>(R.id.hotels) as TextView

        val food_tv = view.findViewById<View>(R.id.food) as TextView

        val transport_tv = view.findViewById<View>(R.id.transport) as TextView


        val infoWindowData = marker.tag as InfoWindowData?

        /* val imageId = context.getResources().getIdentifier(infoWindowData!!.getImage().toLowerCase(),
                 "drawable", context.getPackageName())
         img.setImageResource(imageId)
 */
        //name_tv.setText(infoWindowData!!.full_name)
        // details_tv.setText(infoWindowData.email)

        name_tv.setText(marker.getTitle());
        details_tv.setText(marker.getSnippet())

        hotel_tv.setText("Total Plantation :" + infoWindowData!!.hotel)
        food_tv.setText("Organization Name :" + infoWindowData!!.food)
        transport_tv.setText("Location :" + infoWindowData!!.transport)

        return view
    }
}
