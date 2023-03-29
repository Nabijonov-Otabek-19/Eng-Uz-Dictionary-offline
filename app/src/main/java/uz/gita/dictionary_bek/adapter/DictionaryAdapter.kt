package uz.gita.dictionary_bek.adapter

import android.annotation.SuppressLint
import android.database.Cursor
import android.database.DataSetObserver
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import uz.gita.dictionary_bek.R

class DictionaryAdapter(private var cursor: Cursor) :
    RecyclerView.Adapter<DictionaryAdapter.ViewHolder>() {

    private var isValid = false

    private var clickListener: ((String) -> Unit)? = null
    fun setClickListener(listener: (String) -> Unit) {
        clickListener = listener
    }

    private val notifyingDataSetObserver = object : DataSetObserver() {
        @SuppressLint("NotifyDataSetChanged")
        override fun onChanged() {
            isValid = true
            notifyDataSetChanged()
        }

        @SuppressLint("NotifyDataSetChanged")
        override fun onInvalidated() {
            isValid = false
            notifyDataSetChanged()
        }
    }

    init {
        cursor.registerDataSetObserver(notifyingDataSetObserver)
        isValid = true
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateCursor(newCursor: Cursor) {
        isValid = false
        cursor.unregisterDataSetObserver(notifyingDataSetObserver)
        cursor.close()

        newCursor.registerDataSetObserver(notifyingDataSetObserver)
        cursor = newCursor
        isValid = true
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textWord: TextView = view.findViewById(R.id.text_word)
        val textTranslate: TextView = view.findViewById(R.id.text_translate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_dictionary, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = if (isValid) cursor.count else 0

    @SuppressLint("Range")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        cursor.moveToPosition(position)
        val id = cursor.getLong(cursor.getColumnIndex("id"))
        val word = cursor.getString(cursor.getColumnIndex("english"))
        val translate = cursor.getString(cursor.getColumnIndex("uzbek"))

        holder.itemView.id = id.toInt()
        holder.textWord.text = word
        holder.textTranslate.text = translate

        holder.itemView.setOnClickListener {
            clickListener?.invoke(holder.textWord.text.toString())
        }
    }

    fun onDestroy() {
        cursor.unregisterDataSetObserver(notifyingDataSetObserver)
        cursor.close()
    }
}