package uz.nabijonov.otabek.dictionary_bek.db

import android.database.Cursor

interface DictionaryDao {
    fun getAll(): Cursor
    fun search(word: String, lang: String): Cursor
    fun addFavourite(id: Int)
    fun removeFavourite(id: Int)
    fun getAllFavourites(): Cursor
}