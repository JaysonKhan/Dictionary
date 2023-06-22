package uz.gita.hk_dictionary

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase

interface DictionaryDAO {
    fun getAll():Cursor
    fun search(word:String):Cursor
    fun addFavourite(id:Int)
    fun removeFavourite(id: Int)
    fun getDatabase(): SQLiteDatabase
    fun seperateFavourite(id: Int): Cursor
}