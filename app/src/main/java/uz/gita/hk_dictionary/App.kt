package uz.gita.hk_dictionary

import android.app.Application
import android.content.Context
import android.content.SharedPreferences

class App:Application() {

    companion object{
         lateinit var intance: App
    }

    override fun onCreate() {
        super.onCreate()
        intance = this
    }
}