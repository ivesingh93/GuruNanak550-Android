package eco.com.gurunanak.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import eco.com.gurunanak.R
import kotlinx.android.synthetic.main.fra_550.*


class FrgAbt550 : Fragment() {



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fra_550, container, false)
    }


    override fun onViewCreated(view: View,savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //you can set the title for your toolbar here for different fragments different titles
        activity!!.title = "ABOUT"

        ecosikh.setOnClickListener({


            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(ecosikh.text.toString())
            activity!!.startActivity(i)

        })
    }

}