package uz.nabijonov.otabek.dictionary_bek.db

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor

class SharedPref private constructor() {

    companion object {
        private lateinit var instance: SharedPref

        private const val SHARED_PREF = "shared_pref"
        private const val LANGUAGE = "language"

        private lateinit var pref: SharedPreferences
        private lateinit var editor: Editor

        fun getInstance(context: Context): SharedPref {
            pref = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)
            editor = pref.edit()

            if (!(Companion::instance.isInitialized))
                instance = SharedPref()
            return instance
        }
    }

    var language: String
        set(value) = editor.putString(LANGUAGE, value).apply()
        get() = pref.getString(LANGUAGE, "").toString()
}