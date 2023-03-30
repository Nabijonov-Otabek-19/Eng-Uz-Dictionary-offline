package uz.gita.dictionary_bek.ui.select

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import uz.gita.dictionary_bek.R
import uz.gita.dictionary_bek.databinding.ActivitySelectLanguageBinding
import uz.gita.dictionary_bek.ui.main.MainActivity

class SelectLanguageActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySelectLanguageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectLanguageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            btnEnguz.setOnClickListener {
                val intent = Intent(this@SelectLanguageActivity, MainActivity::class.java)
                intent.putExtra("lang", "english")
                startActivity(intent)
            }

            btnUzeng.setOnClickListener {
                val intent = Intent(this@SelectLanguageActivity, MainActivity::class.java)
                intent.putExtra("lang", "uzbek")
                startActivity(intent)
            }
        }
    }
}