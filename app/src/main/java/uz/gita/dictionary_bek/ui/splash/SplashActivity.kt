package uz.gita.dictionary_bek.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import uz.gita.dictionary_bek.databinding.ActivitySplashBinding
import uz.gita.dictionary_bek.db.SharedPref
import uz.gita.dictionary_bek.ui.main.MainActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val sharedPref by lazy { SharedPref.getInstance(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val time = 1500L

        Handler(Looper.getMainLooper()).postDelayed({
            if (sharedPref.language.isEmpty()) {
                sharedPref.language = "english"
            }
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }, time)
    }

    override fun onPause() {
        super.onPause()
        finish()
    }
}