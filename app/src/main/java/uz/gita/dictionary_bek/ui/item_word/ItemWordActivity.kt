package uz.gita.dictionary_bek.ui.item_word

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import uz.gita.dictionary_bek.R
import uz.gita.dictionary_bek.databinding.ActivityItemWordBinding
import uz.gita.dictionary_bek.db.DBHelper
import uz.gita.dictionary_bek.db.DictionaryDao
import uz.gita.dictionary_bek.model.WordData

class ItemWordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityItemWordBinding
    private val database: DictionaryDao by lazy { DBHelper.getInstance(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItemWordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val item: WordData = intent.getSerializableExtra("item") as WordData
        var like = item.favourite

        binding.apply {
            txtWord.text = item.word
            txtType.text = item.type
            txtTrans.text = item.translate

            if (like == 1) favourite.setImageResource(R.drawable.like)
            else favourite.setImageResource(R.drawable.no_like)

            favourite.setOnClickListener {
                if (like == 1) {
                    favourite.setImageResource(R.drawable.no_like)
                    database.removeFavourite(item.id)
                    like = 0
                } else {
                    favourite.setImageResource(R.drawable.like)
                    database.addFavourite(item.id)
                    like = 1
                }
            }

            btnBack.setOnClickListener {
                finish()
            }
        }
    }
}