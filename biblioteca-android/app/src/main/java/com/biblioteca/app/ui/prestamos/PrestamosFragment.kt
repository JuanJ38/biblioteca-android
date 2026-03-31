package com.biblioteca.app.ui.prestamos

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.biblioteca.app.databinding.FragmentPrestamosBinding
import com.biblioteca.app.model.Libro
import com.biblioteca.app.model.Usuario
import com.biblioteca.app.utils.Resource
import com.biblioteca.app.utils.SessionManager
import com.biblioteca.app.viewmodel.PrestamoViewModel
import com.google.android.material.snackbar.Snackbar

class PrestamosFragment : Fragment() {

    private var _binding: FragmentPrestamosBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PrestamoViewModel by viewModels()
    private lateinit var session: SessionManager
    private lateinit var adapter: PrestamosAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPrestamosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        session = SessionManager(requireContext())

        adapter = PrestamosAdapter(
            onDevolver = { prestamo ->
                AlertDialog.Builder(requireContext())
                    .setTitle("Devolver libro")
                    .setMessage("¿Confirmar devolución de ${prestamo.tituloLibro}?")
                    .setPositiveButton("Confirmar") { _, _ ->
                        viewModel.devolverLibro(session.getBearerToken(), prestamo.id)
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
        )

        binding.recyclerView.adapter = adapter

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.cargarPrestamos(session.getBearerToken())
        }

        binding.fabAgregar.setOnClickListener {
            mostrarDialogoPrestamo()
        }

        viewModel.prestamos.observe(viewLifecycleOwner) { result ->
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

        viewModel.cargarPrestamos(session.getBearerToken())
    }

    private fun mostrarDialogoPrestamo() {
        viewModel.cargarLibrosYUsuarios(session.getBearerToken())

        viewModel.librosDisponibles.observe(viewLifecycleOwner) { libros ->
            viewModel.usuarios.observe(viewLifecycleOwner) { usuarios ->
                if (libros.isEmpty()) {
                    Snackbar.make(binding.root, "No hay libros disponibles", Snackbar.LENGTH_SHORT).show()
                    return@observe
                }

                val layout = LinearLayout(requireContext()).apply {
                    orientation = LinearLayout.VERTICAL
                    setPadding(50, 20, 50, 0)
                }

                val tvLibro = TextView(requireContext()).apply { text = "Libro:" }
                val spinnerLibros = Spinner(requireContext())
                val librosAdapter = ArrayAdapter(requireContext(),
                    android.R.layout.simple_spinner_item,
                    libros.map { it.titulo })
                librosAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerLibros.adapter = librosAdapter

                val tvUsuario = TextView(requireContext()).apply { text = "Usuario:"; setPadding(0, 16, 0, 0) }
                val spinnerUsuarios = Spinner(requireContext())
                val usuariosAdapter = ArrayAdapter(requireContext(),
                    android.R.layout.simple_spinner_item,
                    usuarios.map { it.nombre })
                usuariosAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerUsuarios.adapter = usuariosAdapter

                layout.addView(tvLibro)
                layout.addView(spinnerLibros)
                layout.addView(tvUsuario)
                layout.addView(spinnerUsuarios)

                AlertDialog.Builder(requireContext())
                    .setTitle("Registrar Préstamo")
                    .setView(layout)
                    .setPositiveButton("Registrar") { _, _ ->
                        val libro = libros[spinnerLibros.selectedItemPosition]
                        val usuario = usuarios[spinnerUsuarios.selectedItemPosition]
                        viewModel.registrarPrestamo(session.getBearerToken(), libro.id, usuario.id)
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
