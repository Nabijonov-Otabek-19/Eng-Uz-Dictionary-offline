package uz.gita.dictionary_bek.ui.item_word

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import uz.gita.dictionary_bek.databinding.ActivityItemWordBinding

class ItemWordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityItemWordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItemWordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        val english = intent.getStringExtra("eng")
        val type = intent.getStringExtra("type")
        val uzbek = intent.getStringExtra("uzb")
        val lang = intent.getStringExtra("lang")

        binding.apply {
            if (lang == "english") {
                txtWord.text = english
                txtTrans.text = uzbek
                txtType.text = type

            } else {
                txtWord.text = uzbek
                txtTrans.text = english
                txtType.text = type
            }

            btnBack.setOnClickListener {
                finish()
            }
        }
    }
}