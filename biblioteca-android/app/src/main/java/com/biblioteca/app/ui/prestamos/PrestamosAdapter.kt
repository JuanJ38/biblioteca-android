package com.biblioteca.app.ui.prestamos

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.biblioteca.app.databinding.ItemPrestamoBinding
import com.biblioteca.app.model.Prestamo

class PrestamosAdapter(
    private val onDevolver: (Prestamo) -> Unit
) : ListAdapter<Prestamo, PrestamosAdapter.PrestamoViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrestamoViewHolder {
        val binding = ItemPrestamoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PrestamoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PrestamoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PrestamoViewHolder(private val binding: ItemPrestamoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(prestamo: Prestamo) {
            binding.tvLibro.text = prestamo.tituloLibro
            binding.tvUsuario.text = prestamo.nombreUsuario
            binding.tvFecha.text = "Fecha: ${prestamo.fechaPrestamo}"
            binding.btnDevolver.setOnClickListener { onDevolver(prestamo) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Prestamo>() {
        override fun areItemsTheSame(oldItem: Prestamo, newItem: Prestamo) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Prestamo, newItem: Prestamo) = oldItem == newItem
    }
}
