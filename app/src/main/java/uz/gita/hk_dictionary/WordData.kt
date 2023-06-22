package uz.gita.hk_dictionary

data class WordData(
    val id: Int,
    val word: String,
    val wordType:String,
    val isFavourite: Int,
    val definition: String
)
