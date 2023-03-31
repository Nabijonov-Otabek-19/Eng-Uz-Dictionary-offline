package uz.gita.dictionary_bek.ui.item_word

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import uz.gita.dictionary_bek.R
import uz.gita.dictionary_bek.databinding.ActivityItemWordBinding
import uz.gita.dictionary_bek.db.DBHelper
import uz.gita.dictionary_bek.db.DictionaryDao
import uz.gita.dictionary_bek.db.SharedPref
import uz.gita.dictionary_bek.model.WordData
import java.util.*

class ItemWordActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var binding: ActivityItemWordBinding
    private val database: DictionaryDao by lazy { DBHelper.getInstance(applicationContext) }

    private val sharedPref by lazy { SharedPref.getInstance(applicationContext) }

    private var tts: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItemWordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tts = TextToSpeech(this, this)

        val item: WordData = intent.getSerializableExtra("item") as WordData
        var like = item.favourite

        binding.apply {
            txtWord.text = item.word
            txtType.text = item.type
            txtTrans.text = item.translate

            if (sharedPref.language == "english") {
                btnVolume.visibility = View.VISIBLE
                btnVolume.isClickable = true
            } else {
                btnVolume.visibility = View.INVISIBLE
                btnVolume.isClickable = false
            }

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

            btnVolume.setOnClickListener { speakOut(item.word) }

            btnBack.setOnClickListener { finish() }
        }
    }

    private fun speakOut(word: String) {
        tts!!.speak(word, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts!!.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "The Language is not supported!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    public override fun onDestroy() {
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
        super.onDestroy()
    }
}