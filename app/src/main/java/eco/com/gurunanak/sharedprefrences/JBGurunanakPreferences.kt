package eco.com.gurunanak.sharedprefrences

import android.content.SharedPreferences

object JBGurunanakPreferences {

    fun setJwtToken(preferences: SharedPreferences, email: String) {
        preferences.edit().putString(GurunanakPreferences.JWT_TOKEN, email).commit()
    }

    fun getJwtToken(preferences: SharedPreferences): String? {
        return preferences.getString(GurunanakPreferences.JWT_TOKEN, GurunanakPreferences.JWT_TOKEN_VALUE)
    }

    fun setLoginId(preferences: SharedPreferences, email: String) {
        preferences.edit().putString(GurunanakPreferences.LOGIN_ID, email).commit()
    }

    fun getLoginId(preferences: SharedPreferences): String? {
        return preferences.getString(GurunanakPreferences.LOGIN_ID, GurunanakPreferences.LOGIN_ID_VALUE)
    }

}
