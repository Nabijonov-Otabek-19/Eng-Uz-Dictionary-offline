package uz.gita.dictionary_bek.ui.favourite

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import uz.gita.dictionary_bek.adapter.FavouriteAdapter
import uz.gita.dictionary_bek.databinding.ActivityFavouriteBinding
import uz.gita.dictionary_bek.db.DBHelper
import uz.gita.dictionary_bek.db.DictionaryDao

class FavouriteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavouriteBinding

    private val database: DictionaryDao by lazy { DBHelper.getInstance(applicationContext) }
    private val adapter by lazy { FavouriteAdapter(database.getAllFavourites()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavouriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

            adapter.setClickListener {
                Toast.makeText(this@FavouriteActivity, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onBackPressed() {
        finish()
    }
}