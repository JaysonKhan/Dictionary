package uz.gita.hk_dictionary

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.widget.AppCompatButton
import androidx.viewpager2.widget.ViewPager2
import com.tbuonomo.viewpagerdotsindicator.SpringDotsIndicator

class ViewPagerActivity : AppCompatActivity() {
    private lateinit var viewPager : ViewPager2
    private lateinit var dots: SpringDotsIndicator
    private lateinit var letsGo: AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        setContentView(R.layout.activity_view_pager)

        viewPager = findViewById(R.id.myViewPager)
        dots = findViewById(R.id.spring_dots_indicator)
        letsGo = findViewById(R.id.btn_letsgo)
        val adapter = VpAdapter(supportFragmentManager, lifecycle)
        adapter.setListener {
            letsGo.visibility = View.VISIBLE
        }

        letsGo.setOnClickListener {
            startActivity(Intent(this@ViewPagerActivity, SplashActivity::class.java))
            MySharedPref.getInstance().first = true
        }
        viewPager.adapter = adapter

        dots.attachTo(viewPager)
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