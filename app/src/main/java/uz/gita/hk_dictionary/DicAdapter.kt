package uz.gita.hk_dictionary

import android.annotation.SuppressLint
import android.database.Cursor
import android.database.DataSetObserver
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class DicAdapter(private var cursor: Cursor):RecyclerView.Adapter<DicAdapter.ViewHolder>() {
    private var isValid = false
    private lateinit var listenerFV:((Int, Boolean)->Unit?)
    private lateinit var ttsListener: ((String)->Unit?)
    private lateinit var dialogListener: ((String, String)->Unit)

    private val notifyingDataSetObServer = object : DataSetObserver(){
        @SuppressLint("NotifyDataSetChanged")
        override fun onChanged() {
            isValid = true
            notifyDataSetChanged()
        }

        @SuppressLint("NotifyDataSetChanged")
        override fun onInvalidated() {
            isValid = false
            notifyDataSetChanged()
        }

    }
    init {
        cursor.registerDataSetObserver(notifyingDataSetObServer)
        isValid = true
    }
    fun updateCursor(newCursor: Cursor){
        isValid = false
        cursor.unregisterDataSetObserver(notifyingDataSetObServer)
        cursor.close()

        newCursor.registerDataSetObserver(notifyingDataSetObServer)
        cursor = newCursor
        isValid = true
        notifyDataSetChanged()
    }
    inner class ViewHolder(private val view: View): RecyclerView.ViewHolder(view) {
        val word: TextView = view.findViewById(R.id.englishWord)
        val wordType:TextView =  view.findViewById(R.id.wordType)
        val btn_TTS :ImageView = view.findViewById(R.id.texttospeachebtn)
        val btn_favourite :ImageView = view.findViewById(R.id.favouriteBtn)
        val defenition: TextView = view.findViewById(R.id.definition)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view  = inflater.inflate(R.layout.item_word, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int  = if (isValid) cursor.count else 0

    @SuppressLint("Range")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        cursor.moveToPosition(position)
        val id = cursor.getInt(cursor.getColumnIndex("id"))
        val word = cursor.getString(cursor.getColumnIndex("word"))
        val wordType = cursor.getString(cursor.getColumnIndex("wordtype"))
        val dictionary = cursor.getString(cursor.getColumnIndex("definition")).replace("\n", " ")
        val isFavaurite = cursor.getInt(cursor.getColumnIndex("isRemember"))==1

        holder.word.text = word
        holder.wordType.text = wordType
        holder.defenition.text = dictionary.trim()
        holder.btn_TTS.setOnClickListener {
            ttsListener.invoke(word)
        }
        holder.itemView.setOnClickListener {
            dialogListener.invoke(word, dictionary)
        }
        holder.btn_TTS.setOnLongClickListener {
            ttsListener.invoke(dictionary)
            true
        }

        holder.btn_favourite.setOnClickListener {
            listenerFV.invoke(id, isFavaurite)
        }
        if (isFavaurite){
            holder.btn_favourite.setImageResource(R.drawable.icon_favourite)
        }else{
            holder.btn_favourite.setImageResource(R.drawable.icon_unfavourite)
        }
    }
    fun onDestroy(){
        cursor.unregisterDataSetObserver(notifyingDataSetObServer)
        cursor.close()
    }
    fun setListener(x: ((Int, Boolean)->Unit)){
        listenerFV = x
    }
    fun setTtsListener(x: ((String)->Unit)){
        ttsListener = x
    }
    fun setDialogListener(block: (String, String)->Unit){
        dialogListener = block
    }
}