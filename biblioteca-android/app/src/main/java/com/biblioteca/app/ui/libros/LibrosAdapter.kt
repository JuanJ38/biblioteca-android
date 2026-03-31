package com.biblioteca.app.ui.libros

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.biblioteca.app.databinding.ItemLibroBinding
import com.biblioteca.app.model.Libro

class LibrosAdapter(
    private val onEliminar: (Libro) -> Unit
) : ListAdapter<Libro, LibrosAdapter.LibroViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibroViewHolder {
        val binding = ItemLibroBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LibroViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LibroViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class LibroViewHolder(private val binding: ItemLibroBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(libro: Libro) {
            binding.tvTitulo.text = libro.titulo
            binding.tvAutor.text = libro.autor
            if (libro.disponible) {
                binding.tvEstado.text = "✅ Disponible"
                binding.tvEstado.setTextColor(0xFF155724.toInt())
            } else {
                binding.tvEstado.text = "🔒 Prestado"
                binding.tvEstado.setTextColor(0xFF721C24.toInt())
            }
            binding.btnEliminar.setOnClickListener { onEliminar(libro) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Libro>() {
        override fun areItemsTheSame(oldItem: Libro, newItem: Libro) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Libro, newItem: Libro) = oldItem == newItem
    }
}
