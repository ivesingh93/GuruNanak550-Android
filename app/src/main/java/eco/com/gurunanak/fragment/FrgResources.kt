package eco.com.gurunanak.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import eco.com.gurunanak.R


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class FrgResources : Fragment() {

    private var param1: String? = null
    private var param2: String? = null



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragment_plantation_req, container, false)
    }


    override fun onViewCreated(view: View,savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //you can set the title for your toolbar here for different fragments different titles

    }

    override fun onResume() {
        super.onResume()


        activity!!.title = "Resources"
    }

}