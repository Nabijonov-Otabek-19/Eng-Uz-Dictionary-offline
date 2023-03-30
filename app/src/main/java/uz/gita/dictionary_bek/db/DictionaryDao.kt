package uz.gita.dictionary_bek.db

import android.database.Cursor

interface DictionaryDao {
    fun getAll(lang: String): Cursor
    fun search(word: String, lang: String): Cursor
    fun addFavourite(id: Int)
    fun removeFavourite(id: Int)
    fun getAllFavourites(): Cursor
}