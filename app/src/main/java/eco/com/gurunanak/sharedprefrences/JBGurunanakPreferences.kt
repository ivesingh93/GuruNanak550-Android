package eco.com.gurunanak.sharedprefrences

import android.content.SharedPreferences

object JBGurunanakPreferences {

    fun setOrgName(preferences: SharedPreferences, email: String) {
        preferences.edit().putString(GurunanakPreferences.ORG_NAME, email).commit()
    }

    fun getOrgName(preferences: SharedPreferences): String? {
        return preferences.getString(GurunanakPreferences.ORG_NAME, GurunanakPreferences.ORG_NAME_VALUE)
    }

    fun setLoginId(preferences: SharedPreferences, email: String) {
        preferences.edit().putString(GurunanakPreferences.LOGIN_ID, email).commit()
    }

    fun getLoginId(preferences: SharedPreferences): String? {
        return preferences.getString(GurunanakPreferences.LOGIN_ID, GurunanakPreferences.LOGIN_ID_VALUE)
    }

    fun setJWTToken(preferences: SharedPreferences, email: String) {
        preferences.edit().putString(GurunanakPreferences.JWT_TOKEN, email).commit()
    }

    fun getJWTToken(preferences: SharedPreferences): String? {
        return preferences.getString(GurunanakPreferences.JWT_TOKEN, GurunanakPreferences.JWT_TOKEN_VALUE)
    }


}
