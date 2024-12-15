package ibanez.pppb1.projectuas

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ibanez.pppb1.projectuas.database.NoteResponse
import ibanez.pppb1.projectuas.databinding.ItemMenuBinding

class MenuAdapter(private val isForKatalog: Boolean = false) : RecyclerView.Adapter<MenuAdapter.NoteViewHolder>() {
    private lateinit var binding: ItemMenuBinding
    private var notes = listOf<NoteResponse>()
    private var onItemClickListener: OnItemClickListener? = null
    private var onItemLongClickListener: OnItemLongClickListener? = null
    private var onFavoriteClickListener: OnFavoriteClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(note: NoteResponse)
    }

    interface OnItemLongClickListener {
        fun onItemLongClick(note: NoteResponse)
    }

    interface OnFavoriteClickListener {
        fun onFavoriteClick(note: NoteResponse)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }

    fun setOnItemLongClickListener(listener: OnItemLongClickListener) {
        onItemLongClickListener = listener
    }

    fun setOnFavoriteClickListener(listener: OnFavoriteClickListener) {
        onFavoriteClickListener = listener
    }

    fun setNotes(newNotes: List<NoteResponse>) {
        notes = newNotes
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (isForKatalog) 1 else 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val layout = if (viewType == 0) {
            R.layout.item_menu
        } else {
            R.layout.item_katalog
        }
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        binding = ItemMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(view, binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
        holder.bind(note)

        if (isForKatalog) {
            holder.itemView.findViewById<ImageView>(R.id.imgFavorite).setOnClickListener {
                note.isFavorite = !note.isFavorite
                onFavoriteClickListener?.onFavoriteClick(note)
                notifyItemChanged(position)
            }
        }

        holder.itemView.setOnClickListener {
            onItemClickListener?.onItemClick(note)
        }
        holder.itemView.setOnLongClickListener {
            onItemLongClickListener?.onItemLongClick(note)
            true
        }
    }

    override fun getItemCount(): Int = notes.size

    inner class NoteViewHolder(itemView: View, private val binding: ItemMenuBinding) :
        RecyclerView.ViewHolder(itemView) {
        private val txtNamaMenu: TextView? = itemView.findViewById(R.id.txtNamaMenu)
        private val txtDeskripsiMenu: TextView? = itemView.findViewById(R.id.txtDeskripsiMenu)
        private val txtHarga: TextView? = itemView.findViewById(R.id.txtHarga)


        fun bind(note: NoteResponse) {
            txtNamaMenu?.text = note.nama
            txtDeskripsiMenu?.text = note.deskripsi
            txtHarga?.text = "Rp${note.harga}"
            Log.i("test", note.isFavorite.toString())

            if (isForKatalog) {
                val imgFavorite: ImageView = itemView.findViewById(R.id.imgFavorite)
                if (note.isFavorite) {
                    imgFavorite.setImageResource(R.drawable.baseline_star_24)
                } else {
                    imgFavorite.setImageResource(R.drawable.baseline_star_outline_24)
                }
            }
        }
    }
}
