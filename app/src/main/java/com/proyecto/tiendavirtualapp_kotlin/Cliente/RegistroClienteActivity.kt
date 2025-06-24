package com.proyecto.tiendavirtualapp_kotlin.Cliente


import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.proyecto.tiendavirtualapp_kotlin.Constantes
import com.proyecto.tiendavirtualapp_kotlin.R
import com.proyecto.tiendavirtualapp_kotlin.databinding.ActivityRegistroClienteBinding

class RegistroClienteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegistroClienteBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroClienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por fabor")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.btnRegistrarC.setOnClickListener{
            validarInformacion()
        }


    }
    private var nombres = ""
    private var email = ""
    private var password = ""
    private var cpassword =""
    private fun validarInformacion() {
        nombres = binding.etNombresC.text.toString().trim()
        email = binding.etEmail.text.toString().trim()
        password = binding.etPassword.text.toString().trim()
        cpassword = binding.etCPassword.text.toString().trim()

        when {
            nombres.isEmpty() -> {
                binding.etNombresC.error = "Ingrese nombres"
                binding.etNombresC.requestFocus()
            }
            email.isEmpty() -> {
                binding.etEmail.error = "Ingrese email"
                binding.etEmail.requestFocus()
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                binding.etEmail.error = "Email no válido"
                binding.etEmail.requestFocus()
            }
            password.isEmpty() -> {
                binding.etPassword.error = "Ingrese contraseña"
                binding.etPassword.requestFocus()
            }
            password.length < 6 -> {
                binding.etPassword.error = "Debe tener al menos 6 caracteres"
                binding.etPassword.requestFocus()
            }
            cpassword.isEmpty() -> {
                binding.etCPassword.error = "Confirme la contraseña"
                binding.etCPassword.requestFocus()
            }
            cpassword != password -> {
                binding.etCPassword.error = "Las contraseñas no coinciden"
                binding.etCPassword.requestFocus()
            }

            else -> {
                registrarCliente()
            }
        }
    }

    private fun registrarCliente() {
        progressDialog.setMessage("Creando cuenta")
        progressDialog.show()
        firebaseAuth.createUserWithEmailAndPassword(email,password)
            .addOnSuccessListener {
                insertarInfoBD()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this,"Fallo el registro devido a ${e.message}",Toast.LENGTH_SHORT).show()
            }
    }

    private fun insertarInfoBD() {
        progressDialog.setMessage("Guardando informacion")
        val uid = firebaseAuth.uid
        val nombresC = nombres
        val emailC = email
        val tiempoRegistro = Constantes().obtenerTiempoD()
        val datosCliente = HashMap<String, Any>()

        datosCliente["uid"] = "$uid"
        datosCliente["nombres"] = "$nombresC"
        datosCliente["email"] = "$emailC"
        datosCliente["tiempo_egistro"] = "$tiempoRegistro"
        datosCliente["imagen"] = ""
        datosCliente["tipoUsuario"] = "cliente"
        val reference = FirebaseDatabase.getInstance().getReference("Usuarios")
        reference.child(uid!!)
            .setValue(datosCliente)
            .addOnSuccessListener {
                progressDialog.dismiss()
                startActivity(Intent(this@RegistroClienteActivity, MainActivityCliente ::class.java))
                finishAffinity()
            }
            .addOnFailureListener {e ->
                progressDialog.dismiss()
                Toast.makeText(this,"Fallo el registro devido a ${e.message}",Toast.LENGTH_SHORT).show()
            }
    }
}