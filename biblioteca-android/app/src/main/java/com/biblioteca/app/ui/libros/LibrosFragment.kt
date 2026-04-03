package com.biblioteca.app.ui.libros

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.biblioteca.app.R
import com.biblioteca.app.databinding.FragmentLibrosBinding
import com.biblioteca.app.model.Libro
import com.biblioteca.app.utils.Resource
import com.biblioteca.app.utils.SessionManager
import com.biblioteca.app.viewmodel.LibroViewModel
import com.google.android.material.snackbar.Snackbar

class LibrosFragment : Fragment() {

    private var _binding: FragmentLibrosBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LibroViewModel by viewModels()
    private lateinit var session: SessionManager
    private lateinit var adapter: LibrosAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLibrosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        session = SessionManager(requireContext())

        adapter = LibrosAdapter(
            onEliminar = { libro ->
                AlertDialog.Builder(requireContext())
                    .setTitle("Eliminar libro")
                    .setMessage("¿Eliminar ${libro.titulo}?")
                    .setPositiveButton("Eliminar") { _, _ ->
                        viewModel.eliminarLibro(session.getBearerToken(), libro.id)
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
        )

        binding.recyclerView.adapter = adapter

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.cargarLibros(session.getBearerToken())
        }

        binding.fabAgregar.setOnClickListener {
            mostrarDialogoAgregar()
        }

        viewModel.libros.observe(viewLifecycleOwner) { result ->
            binding.swipeRefresh.isRefreshing = false
            when (result) {
                is Resource.Loading -> binding.progressBar.visibility = View.VISIBLE
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    adapter.submitList(result.data)
                    binding.tvEmpty.visibility = if (result.data.isEmpty()) View.VISIBLE else View.GONE
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Snackbar.make(binding.root, result.message, Snackbar.LENGTH_LONG).show()
                }
            }
        }

        viewModel.operacion.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Success -> Snackbar.make(binding.root, result.data, Snackbar.LENGTH_SHORT).show()
                is Resource.Error -> Snackbar.make(binding.root, result.message, Snackbar.LENGTH_LONG).show()
                else -> {}
            }
        }

        viewModel.cargarLibros(session.getBearerToken())
    }

    private fun mostrarDialogoAgregar() {
        val layout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 20, 50, 0)
        }
        val etTitulo = EditText(requireContext()).apply { hint = "Título" }
        val etAutor = EditText(requireContext()).apply { hint = "Autor" }
        layout.addView(etTitulo)
        layout.addView(etAutor)

        AlertDialog.Builder(requireContext())
            .setTitle("Agregar Libro")
            .setView(layout)
            .setPositiveButton("Guardar") { _, _ ->
                val titulo = etTitulo.text.toString().trim()
                val autor = etAutor.text.toString().trim()
                when {
                    titulo.length < 2 -> Snackbar.make(binding.root, "El título debe tener al menos 2 caracteres", Snackbar.LENGTH_SHORT).show()
                    autor.length < 2 -> Snackbar.make(binding.root, "El autor debe tener al menos 2 caracteres", Snackbar.LENGTH_SHORT).show()
                    else -> viewModel.crearLibro(session.getBearerToken(), Libro(titulo = titulo, autor = autor))
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
