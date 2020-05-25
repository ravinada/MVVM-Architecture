package gomechanic.executiveutils

import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import android.util.Log

class AppStorePreferences(var preference : String, var mode : Int) {
    @Volatile
    private var sharedPreferences: SharedPreferences? = null

    private fun getSharedPreferences(context: Context): SharedPreferences? {
        if (sharedPreferences == null) {
            synchronized(AppStorePreferences::class.java) {
                if (sharedPreferences == null) {
                    sharedPreferences = context.getSharedPreferences(preference, mode)
                }
            }
        }
        return sharedPreferences
    }

    fun registerOnSharedPreferenceChangeListener(context: Context, listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        getSharedPreferences(context)!!.registerOnSharedPreferenceChangeListener(listener)
    }

    fun unregisterOnSharedPreferenceChangeListener(context: Context, listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        getSharedPreferences(context)!!.unregisterOnSharedPreferenceChangeListener(listener)
    }

    fun putInt(context: Context?, key: String, value: Int) {
        if (context != null) {
            getSharedPreferences(context)!!.edit().putInt(key, value).apply()
        }
    }

    fun putString(context: Context?, key: String, value: String) {
        if (context != null) {
            val edit = getSharedPreferences(context)!!.edit()
            if (TextUtils.isEmpty(value)) {
                edit.remove(key)
            } else {
                edit.putString(key, value)
            }
            edit.apply()
        }
    }

    fun putBoolean(context: Context?, key: String, value: Boolean) {
        if (context != null) {
            getSharedPreferences(context)!!.edit().putBoolean(key, value).apply()
        }
    }

    fun getString(context: Context, key: String): String {
        return getSharedPreferences(context)!!.getString(key, "").toString()
    }

    fun getInt(context: Context, key: String): Int {
        return getInt(context, key, 0)
    }

    fun getBoolean(context: Context, key: String): Boolean {
        return getBoolean(context, key, false)
    }

    fun getBoolean(context: Context?, key: String, def: Boolean): Boolean {
        return if (context != null) {
            try {
                getSharedPreferences(context)!!.getBoolean(key, def)
            } catch (ex: ClassCastException) {
                try {
                    val str = getSharedPreferences(context)!!.getString(key, null)
                    if (!TextUtils.isEmpty(str)) {
                        try {
                            val boolValue = java.lang.Boolean.parseBoolean(str)
                            putBoolean(context, key, boolValue)
                            return boolValue
                        } catch (numberExp: Exception) {
                            return def
                        }

                    }
                } catch (e: ClassCastException) {
                    e.printStackTrace()
                }

                def
            }

        } else {
            def
        }

    }

    fun getInt(context: Context?, key: String, fallBack: Int): Int {
        return if (context != null) {
            try {
                getSharedPreferences(context)!!.getInt(key, fallBack)
            } catch (ex: ClassCastException) {
                val str = getSharedPreferences(context)!!.getString(key, null)
                if (!TextUtils.isEmpty(str)) {
                    try {
                        val intValue = Integer.parseInt(str!!)
                        putInt(context, key, intValue)
                        return intValue
                    } catch (numberExp: NumberFormatException) {
                        return fallBack
                    }

                }
                fallBack
            }

        } else {
            fallBack
        }
    }

    fun clearAllSharedPrefrences(context: Context) {
        try {
            getSharedPreferences(context)?.edit()?.let { shared->
                shared.clear()
                shared.apply()
            }
        } catch (ex: ClassCastException) {

        }
    }

}
