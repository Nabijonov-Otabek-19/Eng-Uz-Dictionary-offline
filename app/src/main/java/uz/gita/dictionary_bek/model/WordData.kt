package uz.gita.dictionary_bek.model

data class WordData(
    val id: Long,
    val word: String,
    val type: String,
    val translate: String,
    val favourite: Int
)