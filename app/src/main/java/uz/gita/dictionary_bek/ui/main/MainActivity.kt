package uz.gita.dictionary_bek.ui.main

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import uz.gita.dictionary_bek.db.DBHelper
import uz.gita.dictionary_bek.adapter.DictionaryAdapter
import uz.gita.dictionary_bek.db.DictionaryDao
import uz.gita.dictionary_bek.R

class MainActivity : AppCompatActivity() {
    private val database: DictionaryDao by lazy { DBHelper.getInstance(applicationContext) }
    private val adapter by lazy { DictionaryAdapter(database.getAll()) }

    private lateinit var word: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val rvDictionary: RecyclerView = findViewById(R.id.rv_dictionary)
        val button: Button = findViewById(R.id.btn_search)
        word = findViewById(R.id.input_search
        )
        rvDictionary.adapter = adapter
        rvDictionary.layoutManager = LinearLayoutManager(this)

        button.setOnClickListener {
            val cursor = database.search("${word.text}%")
            adapter.updateCursor(cursor)
        }

        adapter.setClickListener {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        adapter.onDestroy()
    }
}
