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
import uz.gita.dictionary_bek.ui.favourite.FavouriteActivity

class MainActivity : AppCompatActivity() {
    private val database: DictionaryDao by lazy { DBHelper.getInstance(applicationContext) }
    private val adapter by lazy { DictionaryAdapter(database.getAll()) }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            rvDictionary.adapter = adapter
            rvDictionary.layoutManager = LinearLayoutManager(this@MainActivity)

            inputSearch.doOnTextChanged { text, start, before, count ->
                if (text.toString().isNotBlank()) {
                    val cursor = database.search("%$text%")
                    rvDictionary.adapter = adapter
                    adapter.updateCursor(cursor)
                    if (cursor.count == 0) {
                        binding.notFound.visibility = View.VISIBLE
                    } else {
                        binding.notFound.visibility = View.GONE
                    }
                } else {
                    rvDictionary.adapter = DictionaryAdapter(database.getAll())
                }
            }

            btnFavourite.setOnClickListener {
                startActivity(Intent(this@MainActivity, FavouriteActivity::class.java))
            }
        }

        adapter.setClickLikeListener { id, like ->
            if (like == 1) database.removeFavourite(id)
            else database.addFavourite(id)

            adapter.updateCursor(database.getAll())
        }

        adapter.setClickListener {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        adapter.updateCursor(database.getAll())
    }

    override fun onDestroy() {
        super.onDestroy()
        adapter.onDestroy()
    }
}
