package eco.com.gurunanak.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import eco.com.gurunanak.R
import eco.com.gurunanak.sharedprefrences.Prefs
import eco.com.gurunanak.sharedprefrences.SharedPreferencesName
import kotlinx.android.synthetic.main.fragment_profile.*


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class FrgProfile : Fragment() {

    private var param1: String? = null
    private var param2: String? = null



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onResume() {
        super.onResume()
        activity!!.title = "Profile"

    }

    override fun onViewCreated(view: View,savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        txtHeader.setText(Prefs.with(activity!!).getString(SharedPreferencesName.USER_NAME,""))
        txtName.setText("Name - "+Prefs.with(activity!!).getString(SharedPreferencesName.USER_NAME,""))
        txtEmail.setText("Email - "+Prefs.with(activity!!).getString(SharedPreferencesName.EMAIL,""))
        txtCompany.setText("Organization - "+Prefs.with(activity!!).getString(SharedPreferencesName.ORG_NAME,""))
        txtAddress.setText("Full Address - "+Prefs.with(activity!!).getString(SharedPreferencesName.ADDRESS,""))

    }

}