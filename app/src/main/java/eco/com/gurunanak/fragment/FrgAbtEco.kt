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
import kotlinx.android.synthetic.main.frg_gr_sikh.*


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class FrgAbtEco : Fragment() {





    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.frg_gr_sikh, container, false)
    }

    override fun onResume() {
        super.onResume()
        // Set title
        activity!!.title = "About EcoSikh"
    }
    override fun onViewCreated(view: View,savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //you can set the title for your toolbar here for different fragments different titles


        ecosikh2.setOnClickListener({


            val sendIntent = Intent()


            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse("http://www.ecosikh.org/")
            startActivity(i)

        })
    }
    }

