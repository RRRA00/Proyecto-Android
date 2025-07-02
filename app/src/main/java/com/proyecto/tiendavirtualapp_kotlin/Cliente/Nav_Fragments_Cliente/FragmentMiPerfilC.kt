package com.proyecto.tiendavirtualapp_kotlin.Cliente.Nav_Fragments_Cliente

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.proyecto.tiendavirtualapp_kotlin.Cliente.EditarPerfilActivity
import com.proyecto.tiendavirtualapp_kotlin.Modelos.ModeloUsuario
import com.proyecto.tiendavirtualapp_kotlin.R
import com.proyecto.tiendavirtualapp_kotlin.databinding.FragmentMiPerfilCBinding
import java.text.SimpleDateFormat
import java.util.*

class FragmentMiPerfilC : Fragment() {

    private lateinit var binding: FragmentMiPerfilCBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMiPerfilCBinding.inflate(inflater, container, false)

        firebaseAuth = FirebaseAuth.getInstance()

        cargarDatosUsuario()

        binding.btnEditarPerfil.setOnClickListener {
            val intent = Intent(context, EditarPerfilActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }

    private fun cargarDatosUsuario() {
        val uid = firebaseAuth.uid
        if (uid != null) {
            val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
            ref.child(uid).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val usuario = snapshot.getValue(ModeloUsuario::class.java)
                        if (usuario != null) {
                            mostrarDatosUsuario(usuario, snapshot)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Error al cargar datos: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun mostrarDatosUsuario(usuario: ModeloUsuario, snapshot: DataSnapshot) {
        // Mostrar nombres
        binding.tvNombres.text = if (usuario.nombres.isNotEmpty()) usuario.nombres else "No especificado"

        // Mostrar email
        binding.tvEmail.text = if (usuario.email.isNotEmpty()) usuario.email else "No especificado"

        // Mostrar teléfono si existe
        if (usuario.telefono.isNotEmpty()) {
            binding.llTelefono.visibility = View.VISIBLE
            binding.tvTelefono.text = usuario.telefono
        }

        // Mostrar DNI si existe
        if (usuario.dni.isNotEmpty()) {
            binding.llDni.visibility = View.VISIBLE
            binding.tvDni.text = usuario.dni
        }

        // Mostrar proveedor de autenticación
        val proveedor = when (usuario.proveedor) {
            "google" -> "Google"
            "telefono" -> "Teléfono"
            "email" -> "Email"
            else -> "Email"
        }
        binding.tvProveedor.text = proveedor

        // Mostrar fecha de registro
        val fechaRegistro = when {
            usuario.tRegistro.isNotEmpty() -> usuario.tRegistro
            snapshot.child("tiempo_registro").exists() -> "${snapshot.child("tiempo_registro").value}"
            snapshot.child("tiempo_egistro").exists() -> "${snapshot.child("tiempo_egistro").value}"
            else -> ""
        }

        if (fechaRegistro.isNotEmpty()) {
            try {
                val fecha = Date(fechaRegistro.toLong())
                val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                binding.tvFechaRegistro.text = formato.format(fecha)
            } catch (e: Exception) {
                binding.tvFechaRegistro.text = "No disponible"
            }
        } else {
            binding.tvFechaRegistro.text = "No disponible"
        }

        // Cargar imagen de perfil si existe
        if (usuario.imagen.isNotEmpty()) {
            try {
                Glide.with(this)
                    .load(usuario.imagen)
                    .placeholder(R.drawable.ic_person_24)
                    .error(R.drawable.ic_person_24)
                    .into(binding.ivImagenPerfil)
            } catch (e: Exception) {
                // Si hay error al cargar la imagen, usar la imagen por defecto
                binding.ivImagenPerfil.setImageResource(R.drawable.ic_person_24)
            }
        } else {
            binding.ivImagenPerfil.setImageResource(R.drawable.ic_person_24)
        }
    }
}
