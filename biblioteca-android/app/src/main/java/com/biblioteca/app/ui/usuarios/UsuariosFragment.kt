package com.biblioteca.app.ui.usuarios

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.biblioteca.app.databinding.FragmentUsuariosBinding
import com.biblioteca.app.model.Usuario
import com.biblioteca.app.utils.Resource
import com.biblioteca.app.utils.SessionManager
import com.biblioteca.app.viewmodel.UsuarioViewModel
import com.google.android.material.snackbar.Snackbar

class UsuariosFragment : Fragment() {

    private var _binding: FragmentUsuariosBinding? = null
    private val binding get() = _binding!!
    private val viewModel: UsuarioViewModel by viewModels()
    private lateinit var session: SessionManager
    private lateinit var adapter: UsuariosAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentUsuariosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        session = SessionManager(requireContext())

        adapter = UsuariosAdapter(
            onEliminar = { usuario ->
                AlertDialog.Builder(requireContext())
                    .setTitle("Eliminar usuario")
                    .setMessage("¿Eliminar a ${usuario.nombre}?")
                    .setPositiveButton("Eliminar") { _, _ ->
                        viewModel.eliminarUsuario(session.getBearerToken(), usuario.id)
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
        )

        binding.recyclerView.adapter = adapter

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.cargarUsuarios(session.getBearerToken())
        }

        binding.fabAgregar.setOnClickListener {
            mostrarDialogoAgregar()
        }

        viewModel.usuarios.observe(viewLifecycleOwner) { result ->
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

        viewModel.cargarUsuarios(session.getBearerToken())
    }

    private fun mostrarDialogoAgregar() {
        val layout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 20, 50, 0)
        }
        val etNombre = EditText(requireContext()).apply { hint = "Nombre" }
        val etCorreo = EditText(requireContext()).apply { hint = "Correo" }
        layout.addView(etNombre)
        layout.addView(etCorreo)

        AlertDialog.Builder(requireContext())
            .setTitle("Agregar Usuario")
            .setView(layout)
            .setPositiveButton("Guardar") { _, _ ->
                val nombre = etNombre.text.toString().trim()
                val correo = etCorreo.text.toString().trim()
                if (nombre.isNotEmpty() && correo.isNotEmpty()) {
                    viewModel.crearUsuario(session.getBearerToken(), Usuario(nombre = nombre, correo = correo))
                } else {
                    Snackbar.make(binding.root, "Completa todos los campos", Snackbar.LENGTH_SHORT).show()
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
