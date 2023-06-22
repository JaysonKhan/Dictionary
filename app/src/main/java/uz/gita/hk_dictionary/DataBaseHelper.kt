package uz.gita.hk_dictionary

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.io.FileOutputStream
import java.io.InputStream


class DataBaseHelper private constructor(private val context: Context) :
    DBHelper(context, "Dictionary.db", 1), DictionaryDAO {

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: DataBaseHelper? = null

        fun getSingleton(context: Context): DataBaseHelper {
            return instance ?: let {
                val temp = DataBaseHelper(context)
                instance = temp

                temp
            }
        }
        fun getInstance(): DataBaseHelper {
            return instance!!
        }
    }

//    private var database: SQLiteDatabase

//    init {
//        val file = context.getDatabasePath("bit.db")
//        if (!file.exists()) {
//            copyToLocal()
//        }
//        database =
//            SQLiteDatabase.openDatabase(file.absolutePath, null, SQLiteDatabase.OPEN_READWRITE)
//    }

    private fun copyToLocal() {
        val inputStream: InputStream = context.assets.open("Dictionary.db")
        val file = context.getFileStreamPath("bit.db")
        val fileOutputStream = FileOutputStream(file)
        try {
            val byte = ByteArray(1024)
            var length = 0
            while (inputStream.read(byte).also { length = it } > 0) {
                fileOutputStream.write(byte, 0, length)
            }
            fileOutputStream.flush()
        } catch (e: java.lang.Exception) {
            file.delete()
        } finally {
            inputStream.close()
            fileOutputStream.close()
        }
    }

    override fun getAll(): Cursor {
        return database().rawQuery("SELECT * FROM entries", null)
    }

    override fun
            search(word: String): Cursor {
        return database().rawQuery("SELECT * FROM entries WHERE word LIKE ?", arrayOf(word))
    }

    override fun addFavourite(id: Int) {
        database().execSQL("UPDATE entries SET isRemember = 1 WHERE id = $id ")
    }

    override fun removeFavourite(id: Int) {
        database().execSQL("UPDATE entries SET isRemember = 0 WHERE id = $id ")
    }
    override fun seperateFavourite(id: Int):Cursor {
        return database().rawQuery("SELECT * FROM entries WHERE isRemember = $id ", null)
    }
    override fun getDatabase():SQLiteDatabase{
        return database()
    }
}