package uz.gita.dictionary_bek.adapter

import android.annotation.SuppressLint
import android.database.Cursor
import android.database.DataSetObserver
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import uz.gita.dictionary_bek.R

class FavouriteAdapter(private var cursor: Cursor) :
    RecyclerView.Adapter<FavouriteAdapter.ViewHolder>() {

    private var isValid = false

    private var clickListener: ((String) -> Unit)? = null
    private var clickLikeListener: ((Int, Int) -> Unit)? = null

    fun setClickListener(listener: (String) -> Unit) {
        clickListener = listener
    }

    fun setClickLikeListener(like: (Int, Int) -> Unit) {
        clickLikeListener = like
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

    @SuppressLint("Range")
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val textWord: TextView = view.findViewById(R.id.text_word)
        private val imageLike: ImageView = view.findViewById(R.id.imgLike)

        init {
            imageLike.setOnClickListener {
                cursor.moveToPosition(adapterPosition)
                val cursorFav = cursor.getInt(cursor.getColumnIndex("favourite"))
                val cursorId = cursor.getLong(cursor.getColumnIndex("id"))
                if (cursorFav == 1) {
                    imageLike.setImageResource(R.drawable.like)
                } else imageLike.setImageResource(R.drawable.no_like)
                clickLikeListener?.invoke(cursorId.toInt(), cursorFav)
            }

            textWord.setOnClickListener {
                clickListener?.invoke(textWord.text.toString())
            }
        }

        fun bind() {
            cursor.moveToPosition(adapterPosition)
            val isFavourite: Int = cursor.getInt(cursor.getColumnIndex("favourite"))
            val english = cursor.getString(cursor.getColumnIndex("english"))

            textWord.text = english

            if (isFavourite == 1) {
                imageLike.setImageResource(R.drawable.like)
            } else imageLike.setImageResource(R.drawable.no_like)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_dictionary, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = if (isValid) cursor.count else 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind()
    }

    fun onDestroy() {
        cursor.unregisterDataSetObserver(notifyingDataSetObserver)
        cursor.close()
    }
}