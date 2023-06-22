package uz.gita.hk_dictionary

import android.annotation.SuppressLint
import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.gms.tasks.Task
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import uz.gita.hk_dictionary.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity() {
    private val binding by viewBinding(ActivityMainBinding::bind)
    private val database: DictionaryDAO by lazy { DataBaseHelper.getSingleton(applicationContext) }
    private val adapter by lazy { DicAdapter(database.getAll()) }
    private lateinit var tts: TextToSpeech
    private var id = 1
    lateinit var cursor: Cursor
    private var isClicked = false
    private val searchClickedLiveData = MutableLiveData<Boolean>()
    private lateinit var searchView: SearchView
    private lateinit var reviewManager: ReviewManager
    private lateinit var reviewInfo: ReviewInfo
    private lateinit var emptylist: TextView

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        emptylist = binding.placeHolder
        searchClickedLiveData.observe(this, clickObserver)
        requestReviewInfo()


        binding.apply {
            val toolbar: Toolbar = include.myToolBar
            setSupportActionBar(toolbar)
            toolbar.setNavigationIcon(R.drawable.khan)
            toolbar.setNavigationOnClickListener {
                showReviewFlow()
                Toast.makeText(this@MainActivity, "With KHAN347...", Toast.LENGTH_SHORT).show()
            }
            adapter.setListener { id, state ->
                if (state) {
                    database.removeFavourite(id)
                } else {
                    database.addFavourite(id)
                }
                if (isClicked) {
                    cursor = database.seperateFavourite(1)
                } else {
                    cursor = database.search("${searchView.query}%")
                }
                checkEmpty()
                adapter.updateCursor(cursor)
            }
            adapter.setTtsListener { text ->
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null)

            }
            rvDictionary.adapter = adapter
            rvDictionary.layoutManager = LinearLayoutManager(this@MainActivity)

            adapter.setDialogListener { word, dictionary ->
                AlertDialog.Builder(this@MainActivity)
                    .setTitle(word)
                    .setMessage(dictionary)
                    .setCancelable(false)
                    .setIcon(getDrawable(R.drawable.nook))
                    .setNegativeButton("Close") { dialog, which ->
                        dialog.cancel()
                        tts.stop()
                    }
                    .setNeutralButton("Share or Copy") { dialog, _ ->
                        val sendIntent: Intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "We express the `$word` image as follows: \n\n $dictionary https://play.google.com/store/apps/details?id=uz.gita.hk_dictionary"
                            )
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, null)
                        startActivity(shareIntent)
                    }
                    .create()
                    .show()
            }

        }

        tts = TextToSpeech(this) { status ->
            if (status != TextToSpeech.ERROR) {
                val speechRate = 0.85f
                tts.setSpeechRate(speechRate)
                val maleVoice = Locale("en", "US")
                tts.setLanguage(maleVoice)
                tts.language = Locale.US

            }
        }
    }

    private fun checkEmpty() {
        if (cursor.count==0){
            emptylist.visibility = View.VISIBLE
        }else{
            emptylist.visibility = View.GONE
        }
    }

    private val clickObserver = Observer<Boolean> {
        if (it) {
            cursor = database.getAll()
        } else {
            cursor = database.seperateFavourite(id)
        }
        checkEmpty()
        adapter.updateCursor(cursor)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        val onExpendListener = object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                Toast.makeText(this@MainActivity, "EXPAND", Toast.LENGTH_SHORT).show()
                cursor = database.seperateFavourite(id)
                checkEmpty()
                adapter.updateCursor(cursor)
                menu.findItem(R.id.icon_favourite).isVisible = false
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                Toast.makeText(this@MainActivity, "COLLAPSE", Toast.LENGTH_SHORT).show()
                menu.findItem(R.id.icon_favourite).isVisible = true
                return true
            }
        }
        menu.findItem(R.id.icon_search)?.setOnActionExpandListener(onExpendListener)
        searchView = menu.findItem(R.id.icon_search).actionView as SearchView
        searchView.queryHint = "Search word..."
        searchView.imeOptions = EditorInfo.IME_ACTION_SEARCH
        val lestener = object : SearchView.OnQueryTextListener {
            @SuppressLint("Range")
            override fun onQueryTextSubmit(query: String?): Boolean {
                cursor = database.search("${query}%")
                checkEmpty()
                adapter.updateCursor(cursor)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                cursor = database.search("${newText}%")
                checkEmpty()
                adapter.updateCursor(cursor)
                return true
            }
        }
        searchView.setOnQueryTextListener(lestener)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.icon_search -> {
            searchClickedLiveData.value = false
            true
        }
        R.id.icon_favourite -> {
            if (id == 0) {
                isClicked = false
                item.setTitle("Saved words")
                cursor = database.getAll()

            } else {
                isClicked = true
                item.setTitle("All words")
                cursor  = database.seperateFavourite(id)
            }
            id = if (id == 1) 0 else 1
            checkEmpty()
            adapter.updateCursor(cursor)
            true
        }
        else -> {
            true
        }
    }

    private fun requestReviewInfo() {
        reviewManager = ReviewManagerFactory.create(this)
        val request: Task<ReviewInfo> = reviewManager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                reviewInfo = task.result
            } else {
                Toast.makeText(this, "ReviewInfo not received.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showReviewFlow() {
        if (::reviewInfo.isInitialized) {
            val flow: Task<Void> = reviewManager.launchReviewFlow(this, reviewInfo)
            flow.addOnCompleteListener {
                Toast.makeText(this, "Review successful..", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        adapter.onDestroy()
    }
}