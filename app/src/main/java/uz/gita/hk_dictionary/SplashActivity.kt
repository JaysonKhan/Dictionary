package uz.gita.hk_dictionary

import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.ProgressBar
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class SplashActivity : AppCompatActivity() {
    private lateinit var process: ProgressBar

    init {
        val thread = Executors.newSingleThreadExecutor()
        thread.execute {
            thread.awaitTermination(5, TimeUnit.SECONDS)
            if (MySharedPref.getInstance().first){
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            }else{
                startActivity(Intent(this@SplashActivity, ViewPagerActivity::class.java))
            }
            thread.shutdown()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        setContentView(R.layout.activity_splash)
        process = findViewById(R.id.progressBar)

        val animation = ObjectAnimator.ofInt(process, "progress", 0, 100)
        animation.duration = 6000
        animation.interpolator = DecelerateInterpolator()
        animation.start()
    }

    override fun onRestart() {
        super.onRestart()
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE

    }
}