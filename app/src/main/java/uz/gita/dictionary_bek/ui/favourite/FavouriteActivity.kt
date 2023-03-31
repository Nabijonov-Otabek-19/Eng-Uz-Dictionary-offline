package uz.gita.dictionary_bek.ui.favourite

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import uz.gita.dictionary_bek.adapter.FavouriteAdapter
import uz.gita.dictionary_bek.databinding.ActivityFavouriteBinding
import uz.gita.dictionary_bek.db.DBHelper
import uz.gita.dictionary_bek.db.DictionaryDao
import uz.gita.dictionary_bek.ui.item_word.ItemWordActivity

class FavouriteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavouriteBinding

    private val database: DictionaryDao by lazy { DBHelper.getInstance(applicationContext) }
    private val lang by lazy { intent.getStringExtra("lang") }
    private val adapter by lazy { FavouriteAdapter(database.getAllFavourites(), lang!!) }

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

            adapter.setClickListener { item ->
                val intent = Intent(this@FavouriteActivity, ItemWordActivity::class.java)
                intent.putExtra("item", item)
                startActivity(intent)
            }
        }
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        adapter.onDestroy()
    }
}