package com.proyecto.tiendavirtualapp_kotlin.Cliente

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.proyecto.tiendavirtualapp_kotlin.Modelos.ModeloUsuario
import com.proyecto.tiendavirtualapp_kotlin.R
import com.proyecto.tiendavirtualapp_kotlin.databinding.ActivityEditarPerfilBinding

class EditarPerfilActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditarPerfilBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    private var imageUri: Uri? = null
    private var usuarioActual: ModeloUsuario? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditarPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        configurarEventos()
        cargarDatosUsuario()
    }

    private fun configurarEventos() {
        binding.btnRegresar.setOnClickListener {
            finish()
        }

        binding.btnCancelar.setOnClickListener {
            finish()
        }

        binding.btnCambiarImagen.setOnClickListener {
            seleccionarImagen()
        }

        binding.btnGuardar.setOnClickListener {
            validarYGuardarCambios()
        }
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
                            usuarioActual = usuario
                            mostrarDatosEnFormulario(usuario)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@EditarPerfilActivity, "Error al cargar datos: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun mostrarDatosEnFormulario(usuario: ModeloUsuario) {
        binding.etNombres.setText(usuario.nombres)
        binding.etTelefono.setText(usuario.telefono)
        binding.etDni.setText(usuario.dni)
        binding.etEmail.setText(usuario.email)

        // Cargar imagen de perfil si existe
        if (usuario.imagen.isNotEmpty()) {
            try {
                Glide.with(this)
                    .load(usuario.imagen)
                    .placeholder(R.drawable.ic_person_24)
                    .error(R.drawable.ic_person_24)
                    .into(binding.ivImagenPerfil)
            } catch (e: Exception) {
                binding.ivImagenPerfil.setImageResource(R.drawable.ic_person_24)
            }
        } else {
            binding.ivImagenPerfil.setImageResource(R.drawable.ic_person_24)
        }
    }

    private fun seleccionarImagen() {
        ImagePicker.with(this)
            .crop()
            .compress(1024)
            .maxResultSize(1080, 1080)
            .createIntent { intent ->
                resultadoImg.launch(intent)
            }
    }

    private val resultadoImg = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { resultado ->
        if (resultado.resultCode == Activity.RESULT_OK) {
            val data = resultado.data
            imageUri = data?.data
            binding.ivImagenPerfil.setImageURI(imageUri)
        } else {
            Toast.makeText(this, "Selección de imagen cancelada", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validarYGuardarCambios() {
        val nombres = binding.etNombres.text.toString().trim()
        val telefono = binding.etTelefono.text.toString().trim()
        val dni = binding.etDni.text.toString().trim()

        if (nombres.isEmpty()) {
            binding.etNombres.error = "Ingrese su nombre"
            binding.etNombres.requestFocus()
            return
        }

        if (telefono.isNotEmpty() && telefono.length < 9) {
            binding.etTelefono.error = "Teléfono debe tener al menos 9 dígitos"
            binding.etTelefono.requestFocus()
            return
        }

        if (dni.isNotEmpty() && dni.length != 8) {
            binding.etDni.error = "DNI debe tener 8 dígitos"
            binding.etDni.requestFocus()
            return
        }

        guardarCambios(nombres, telefono, dni)
    }

    private fun guardarCambios(nombres: String, telefono: String, dni: String) {
        progressDialog.setMessage("Guardando cambios...")
        progressDialog.show()

        val uid = firebaseAuth.uid
        if (uid != null) {
            val ref = FirebaseDatabase.getInstance().getReference("Usuarios").child(uid)

            // Preparar los datos a actualizar
            val datosActualizados = hashMapOf<String, Any>(
                "nombres" to nombres,
                "telefono" to telefono,
                "dni" to dni
            )

            // Si hay una nueva imagen, subirla primero
            if (imageUri != null) {
                subirImagen(uid, datosActualizados)
            } else {
                // Solo actualizar los datos sin imagen
                actualizarDatos(ref, datosActualizados)
            }
        }
    }

    private fun subirImagen(uid: String, datosActualizados: HashMap<String, Any>) {
        val nombreImagen = "perfil_$uid"
        val storageRef = FirebaseStorage.getInstance().getReference("ImagenesPerfiles/$nombreImagen")

        storageRef.putFile(imageUri!!)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    datosActualizados["imagen"] = uri.toString()
                    val ref = FirebaseDatabase.getInstance().getReference("Usuarios").child(uid)
                    actualizarDatos(ref, datosActualizados)
                }
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(this, "Error al subir imagen: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun actualizarDatos(ref: com.google.firebase.database.DatabaseReference, datosActualizados: HashMap<String, Any>) {
        ref.updateChildren(datosActualizados)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this, "Perfil actualizado exitosamente", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(this, "Error al actualizar perfil: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
