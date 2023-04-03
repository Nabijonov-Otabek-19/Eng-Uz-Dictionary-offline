package uz.gita.dictionary_bek.ui.favourite

import android.app.Dialog
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
import androidx.recyclerview.widget.LinearLayoutManager
import uz.gita.dictionary_bek.R
import uz.gita.dictionary_bek.adapter.FavouriteAdapter
import uz.gita.dictionary_bek.databinding.ActivityFavouriteBinding
import uz.gita.dictionary_bek.db.DBHelper
import uz.gita.dictionary_bek.db.DictionaryDao
import uz.gita.dictionary_bek.db.SharedPref
import uz.gita.dictionary_bek.model.WordData
import java.util.*

class FavouriteActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var binding: ActivityFavouriteBinding

    private val database: DictionaryDao by lazy { DBHelper.getInstance(applicationContext) }
    private val lang by lazy { intent.getStringExtra("lang") }
    private val sharedPref by lazy { SharedPref.getInstance(applicationContext) }
    private val adapter by lazy { FavouriteAdapter(database.getAllFavourites(), lang!!) }

    private var tts: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavouriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tts = TextToSpeech(this, this)

        binding.apply {
            recyclerFav.adapter = adapter
            recyclerFav.layoutManager = LinearLayoutManager(this@FavouriteActivity)

            val cursor = database.getAllFavourites()
            if (cursor.count == 0) {
                noFav.visibility = View.VISIBLE
            } else noFav.visibility = View.INVISIBLE

            btnBack.setOnClickListener {
                finish()
            }

            adapter.setClickLikeListener { id, like ->
                if (like == 1) {
                    database.removeFavourite(id)
                } else database.addFavourite(id)

                adapter.updateCursor(database.getAllFavourites())

                if (database.getAllFavourites().count == 0) {
                    noFav.visibility = View.VISIBLE
                } else noFav.visibility = View.INVISIBLE
            }

            adapter.setClickListener { item ->
                showItemDialog(item)
            }
        }
    }

    private fun showItemDialog(item: WordData) {
        val dialog = Dialog(this@FavouriteActivity)

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

    override fun onBackPressed() {
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        adapter.onDestroy()
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