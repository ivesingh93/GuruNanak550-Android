package eco.com.gurunanak.sharedprefrences

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


/**
 * Created by naveen.mishra on 12/18/2017.
 */
class Prefs {
    companion object {

    var mcontext: Context? = null
    private val TAG = "Prefs"

    var singleton: Prefs? = null

    lateinit var preferences: SharedPreferences

    var editor: SharedPreferences.Editor? = null
     val GSON = Gson()
    var typeOfObject = object : TypeToken<Any>() {

    }.type

    fun Prefs(context:Context) {

        preferences=context.getSharedPreferences(TAG,Context.MODE_PRIVATE)
        editor=preferences.edit()
        this.mcontext=context
    }

    fun with(context: Context): Prefs {
        Companion.Prefs(context)
        if (singleton == null) {
            singleton = Builder(context).build()
        }

        return singleton as Prefs
    }


    private class Builder(context: Context?) {

        private val context: Context

        init {
            if (context == null) {
                throw IllegalArgumentException("Context must not be null.")
            }
            this.context = context.applicationContext
        }

        /**
         * Method that creates an instance of Prefs
         *
         * @return an instance of Prefs
         */
        fun build(): Prefs {
            return Prefs()
        }
    }


}

    fun save(key: String, value: Boolean) {
        editor?.putBoolean(key, value)?.apply()
    }

    fun save(key: String, value: String) {


        try{
            Log.e(key,""+value+"");
            editor?.putString(key, value)!!.apply()
        }catch (e:Exception){e.printStackTrace()}
    }

    fun save(key: String, value: Int) {
        try{
            Log.e(key,""+value+"");
            editor?.putInt(key, value)?.apply()
        }catch (e:Exception){}

    }

    fun save(key: String, value: Float) {
        editor?.putFloat(key, value)?.apply()
    }

    fun save(key: String, value: Long) {
        editor?.putLong(key, value)?.apply()
    }

    fun save(key: String, value: Set<String>) {
        editor?.putStringSet(key, value)?.apply()
    }

    // to save object in prefrence
    fun save(key: String?, `object`: Any?) {
        if (`object` == null) {
            throw IllegalArgumentException("object is null")
        }

        if (key == "" || key == null) {
            throw IllegalArgumentException("key is empty or null")
        }

        editor?.putString(key, GSON.toJson(`object`))?.apply()
    }

    // To get object from prefrences

    fun <T> getObject(key: String, a: Class<T>): T? {

        val gson = preferences?.getString(key, null)
        return if (gson == null) {
            null
        } else {
            try {
                GSON.fromJson(gson, a)
            } catch (e: Exception) {
                throw IllegalArgumentException("Object storaged with key $key is instanceof other class")
            }

        }
    }

    fun getBoolean(key: String, defValue: Boolean): Boolean {
        return preferences?.getBoolean(key, defValue)!!
    }

    fun getString(key: String, defValue: String): String {

        try {
            return preferences?.getString(key, defValue)!!
        } catch (e: Exception) {
            return ""
        }

    }

    fun getInt(key: String, defValue: Int): Int {
        return preferences?.getInt(key, defValue)!!
    }

    fun getFloat(key: String, defValue: Float): Float {
        return preferences?.getFloat(key, defValue)!!
    }

    fun getLong(key: String, defValue: Long): Long {
        return preferences?.getLong(key, defValue)!!
    }

    fun getStringSet(key: String, defValue: Set<String>): Set<String> {
        return preferences?.getStringSet(key, defValue)!!
    }

    fun getAll(): Map<String, *> {
        return preferences?.getAll()!!
    }

    fun remove(key: String) {
        editor?.remove(key)?.apply()
    }

    fun removeAll() {
        editor?.clear()
        editor?.apply()

    }




}