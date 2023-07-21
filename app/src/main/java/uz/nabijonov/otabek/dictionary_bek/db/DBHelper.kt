package uz.nabijonov.otabek.dictionary_bek.db

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import uz.nabijonov.otabek.dictionary_bek.utils.Constants
import java.io.FileOutputStream
import java.io.InputStream

class DBHelper private constructor(private val context: Context) :
    SQLiteOpenHelper(context, Constants.DB_NAME, null, Constants.VERSION), DictionaryDao {

    companion object {
        @SuppressLint("StaticFieldLeak")
        private lateinit var instance: DBHelper

        fun getInstance(context: Context): DBHelper {
            if (!(Companion::instance.isInitialized)) {
                instance = DBHelper(context)
            }
            return instance
        }
    }

    private var database: SQLiteDatabase

    init {
        val file = context.getDatabasePath("localDictionary.db")
        if (!file.exists()) {
            copyToLocal()
        }
        database =
            SQLiteDatabase.openDatabase(file.absolutePath, null, SQLiteDatabase.OPEN_READWRITE)
    }

    override fun onCreate(db: SQLiteDatabase) {
        //db.execSQL("CREATE TABLE 'students'( );")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

    }

    private fun copyToLocal() {
        val inputStream: InputStream = context.assets.open(Constants.DBFILE_NAME)
        val file = context.getDatabasePath("localDictionary.db")
        val fileOutputStream = FileOutputStream(file)
        try {
            val byte = ByteArray(1024)
            var length: Int
            while (inputStream.read(byte).also { length = it } > 0) {
                fileOutputStream.write(byte, 0, length)
            }
            fileOutputStream.flush()
        } catch (e: Exception) {
            file.delete()
        } finally {
            inputStream.close()
            fileOutputStream.close()
        }
    }

    @SuppressLint("Range")
    override fun getAll(): Cursor {
        return database.rawQuery("SELECT * FROM dictionary", null)
    }

    override fun search(word: String, lang: String): Cursor {
        return database.rawQuery("SELECT * FROM dictionary WHERE $lang LIKE ?", arrayOf(word))
    }

    override fun addFavourite(id: Int) {
        database.execSQL("UPDATE dictionary set favourite=1 WHERE id = $id")
    }

    override fun removeFavourite(id: Int) {
        database.execSQL("""UPDATE dictionary set favourite=0 WHERE id = $id""".trimEnd())
    }

    override fun getAllFavourites(): Cursor {
        return database.rawQuery("SELECT * FROM dictionary WHERE favourite = 1", null)
    }
}