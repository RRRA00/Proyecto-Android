package com.proyecto.tiendavirtualapp_kotlin.Vendedor

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.proyecto.tiendavirtualapp_kotlin.Constantes
import com.proyecto.tiendavirtualapp_kotlin.databinding.ActivityRegistroVendedorBinding


class RegistroVendedorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegistroVendedorBinding
    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroVendedorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.btnRegistrarV.setOnClickListener{
            validarInformacion()
        }

    }

    private var nombres = ""
    private var email = ""
    private var password = ""
    private var cpassword = ""

    private fun showError(field: EditText, message: String): Boolean {
        field.error = message
        field.requestFocus()
        return false
    }

    private fun validarInformacion() {
        nombres = binding.etNombresV.text.toString().trim()
        email = binding.etEmail.text.toString().trim()
        password = binding.etPassword.text.toString().trim()
        cpassword = binding.etCPassword.text.toString().trim()
        email = binding.etEmail.text.toString().trim()
        password = binding.etPassword.text.toString().trim()
        cpassword = binding.etCPassword.text.toString().trim()

        val isValid = when {
            nombres.isEmpty() -> showError(binding.etNombresV, "Ingrese sus nombres")
            email.isEmpty() -> showError(binding.etEmail, "Ingrese su Email")
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> showError(binding.etEmail, "Email no válido")
            password.isEmpty() -> showError(binding.etPassword, "Ingrese Password")
            password.length < 6 -> showError(binding.etPassword, "Necesita 6 o más caracteres")
            cpassword.isEmpty() -> showError(binding.etCPassword, "Confirme password")
            password != cpassword -> showError(binding.etCPassword, "No coincide")
            else -> true
        }

        if (isValid) {
            registrarVendedor()
        }

    }

    private fun registrarVendedor() {
        progressDialog.setMessage("Creando Cuenta")
        progressDialog.show()

        firebaseAuth.createUserWithEmailAndPassword(email,password)
            .addOnSuccessListener {
                insertarInfoBD()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this,"Falló el registro debido a ${e.message}",Toast.LENGTH_SHORT).show()
            }
    }

    private fun insertarInfoBD() {
        progressDialog.setMessage("Guardar informacion...")

        val uidBD = firebaseAuth.uid
        val nombreBD = nombres
        val emailBD = email
        val tiempoBD = Constantes().obtenerTiempoD()
        val datosVendedor = HashMap<String,Any>()

        datosVendedor["uid"] = "$uidBD"
        datosVendedor["nombres"] = "$nombreBD"
        datosVendedor["email"] = "$emailBD"
        datosVendedor["tipoUsuario"] = "vendedor"
        datosVendedor["tiempo_registro"] = tiempoBD

        val reference = FirebaseDatabase.getInstance().getReference("Usuarios")
        reference.child(uidBD!!)
            .setValue(datosVendedor)
            .addOnSuccessListener {
                progressDialog.dismiss()
                startActivity(Intent(this,MainActivityVendedor::class.java))
                finish()
            }
            .addOnFailureListener { e->
                progressDialog.dismiss()
                Toast.makeText(this,"Falló el registro en BD debido a ${e.message}",Toast.LENGTH_SHORT).show()
            }

    }
}