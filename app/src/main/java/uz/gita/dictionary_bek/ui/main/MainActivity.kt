package uz.gita.dictionary_bek.ui.main

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import uz.gita.dictionary_bek.R
import uz.gita.dictionary_bek.adapter.DictionaryAdapter
import uz.gita.dictionary_bek.databinding.ActivityMainBinding
import uz.gita.dictionary_bek.db.DBHelper
import uz.gita.dictionary_bek.db.DictionaryDao
import uz.gita.dictionary_bek.db.SharedPref
import uz.gita.dictionary_bek.model.WordData
import uz.gita.dictionary_bek.ui.favourite.FavouriteActivity
import java.util.*

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private val database: DictionaryDao by lazy { DBHelper.getInstance(applicationContext) }
    private val sharedPref by lazy { SharedPref.getInstance(applicationContext) }
    private val adapter by lazy { DictionaryAdapter(database.getAll()) }

    private var tts: TextToSpeech? = null

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tts = TextToSpeech(this, this)

        binding.apply {
            adapter.setLang(sharedPref.language)
            rvDictionary.adapter = adapter
            rvDictionary.layoutManager = LinearLayoutManager(this@MainActivity)

            if (sharedPref.language == "english") inputSearch.hint = "Search - Qidiruv"
            else inputSearch.hint = "Qidiruv - Search"

            inputSearch.doOnTextChanged { text, start, before, count ->
                if (text.toString().isNotBlank()) {
                    val cursor = database.search("%$text%", sharedPref.language)
                    rvDictionary.adapter = adapter
                    adapter.updateCursor(cursor)
                    if (cursor.count == 0) {
                        binding.notFound.visibility = View.VISIBLE
                    } else {
                        binding.notFound.visibility = View.GONE
                    }
                } else {
                    adapter.updateCursor(database.getAll())
                    rvDictionary.adapter = adapter
                }
            }

            btnFavourite.setOnClickListener {
                val intent = Intent(this@MainActivity, FavouriteActivity::class.java)
                intent.putExtra("lang", sharedPref.language)
                startActivity(intent)
            }

            btnSwap.setOnClickListener {
                if (sharedPref.language == "english") {
                    sharedPref.language = "uzbek"
                    Toast.makeText(this@MainActivity, "Uzbek-English", Toast.LENGTH_SHORT).show()
                } else {
                    sharedPref.language = "english"
                    Toast.makeText(this@MainActivity, "English-Uzbek", Toast.LENGTH_SHORT).show()
                }
                adapter.setLang(sharedPref.language)
                adapter.updateCursor(database.getAll())
                rvDictionary.adapter = adapter
            }
        }

        adapter.setClickLikeListener { id, like ->
            if (like == 1) database.removeFavourite(id)
            else database.addFavourite(id)

            adapter.updateCursor(database.getAll())
        }

        adapter.setClickListener {
            showItemDialog(it)
        }
    }

    private fun showItemDialog(item: WordData) {
        val dialog = Dialog(this@MainActivity)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT

        val window = dialog.window
        window!!.attributes = lp

        dialog.setContentView(R.layout.custom_word_dialog)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val word: AppCompatTextView = dialog.findViewById(R.id.txtWord)
        val type: AppCompatTextView = dialog.findViewById(R.id.txtType)
        val transcript: AppCompatTextView = dialog.findViewById(R.id.txtTranscript)
        val translate: AppCompatTextView = dialog.findViewById(R.id.txtTrans)

        val audio: AppCompatImageView = dialog.findViewById(R.id.btnVolume)
        val btnClose: AppCompatButton = dialog.findViewById(R.id.btnClose)

        item.apply {
            word.text = this.word
            type.text = this.type
            transcript.text = this.transcript
            translate.text = this.translate

            if (sharedPref.language == "english") {
                audio.visibility = View.VISIBLE
                audio.isClickable = true
            } else {
                audio.visibility = View.INVISIBLE
                audio.isClickable = false
            }
        }

        audio.setOnClickListener {
            tts!!.speak(item.word, TextToSpeech.QUEUE_FLUSH, null, "")
        }

        btnClose.setOnClickListener {
            dialog.dismiss()
        }
        dialog.create()
        dialog.show()
    }

    override fun onResume() {
        super.onResume()
        adapter.updateCursor(database.getAll())
    }

    override fun onDestroy() {
        super.onDestroy()
        adapter.onDestroy()
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts!!.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "The Language is not supported!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
