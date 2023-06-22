package uz.gita.hk_dictionary

import android.content.Context

class MySharedPref {
    companion object {
        private val instance = MySharedPref()
        private val pref = App.intance.getSharedPreferences("ContactApp", Context.MODE_PRIVATE)

        fun getInstance() = instance
    }

    var first: Boolean
        set(bool) = pref.edit().putBoolean("ISFIRST", bool).apply()
        get() = pref.getBoolean("ISFIRST", false)

}