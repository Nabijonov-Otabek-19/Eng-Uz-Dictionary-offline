package uz.gita.dictionary_bek.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import uz.gita.dictionary_bek.adapter.DictionaryAdapter
import uz.gita.dictionary_bek.databinding.ActivityMainBinding
import uz.gita.dictionary_bek.db.DBHelper
import uz.gita.dictionary_bek.db.DictionaryDao
import uz.gita.dictionary_bek.db.SharedPref
import uz.gita.dictionary_bek.ui.favourite.FavouriteActivity
import uz.gita.dictionary_bek.ui.item_word.ItemWordActivity

class MainActivity : AppCompatActivity() {
    private val database: DictionaryDao by lazy { DBHelper.getInstance(this) }
    private val sharedPref by lazy { SharedPref.getInstance(this) }
    private lateinit var adapter: DictionaryAdapter

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = DictionaryAdapter(database.getAll(), sharedPref.language)

        binding.apply {
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
                    adapter = DictionaryAdapter(database.getAll(), sharedPref.language)
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
                adapter = DictionaryAdapter(database.getAll(), sharedPref.language)
                rvDictionary.adapter = adapter
            }
        }

        adapter.setClickLikeListener { id, like ->
            if (like == 1) database.removeFavourite(id)
            else database.addFavourite(id)

            adapter.updateCursor(database.getAll())
        }

        adapter.setClickListener {
            val intent = Intent(this, ItemWordActivity::class.java)
            intent.putExtra("item", it)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        adapter = DictionaryAdapter(database.getAll(), sharedPref.language)
        binding.rvDictionary.adapter = adapter
        if (sharedPref.language.isEmpty()) sharedPref.language = "english"
        adapter.updateCursor(database.getAll())
    }

    override fun onDestroy() {
        super.onDestroy()
        adapter.onDestroy()
    }
}
