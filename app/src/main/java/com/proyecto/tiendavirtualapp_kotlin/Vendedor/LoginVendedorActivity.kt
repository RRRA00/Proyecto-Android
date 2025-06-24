package com.proyecto.tiendavirtualapp_kotlin.Vendedor

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.proyecto.tiendavirtualapp_kotlin.R
import com.proyecto.tiendavirtualapp_kotlin.databinding.ActivityLoginVendedorBinding
import com.proyecto.tiendavirtualapp_kotlin.databinding.ActivityRegistroVendedorBinding

class LoginVendedorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginVendedorBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginVendedorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.btnLoginV.setOnClickListener{
            validarInfo()
        }

        binding.tvRegistrarV.setOnClickListener {
            startActivity(Intent(applicationContext,RegistroVendedorActivity::class.java))
        }

    }

    private var email = ""
    private var password = ""


    private fun validarInfo() {
        email = binding.etEmail.text.toString().trim()
        password = binding.etPassword.text.toString().trim()
        val isValid = when {
            email.isEmpty() -> showError(binding.etEmail, "Ingrese su Email")
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> showError(binding.etEmail, "Email no válido")
            password.isEmpty() -> showError(binding.etPassword, "Ingrese Password")
            else -> true
        }
        if (isValid) {
            loginVendedor()
        }
    }
    private fun loginVendedor() {
        progressDialog.setMessage("Ingresando")
        progressDialog.show()

        firebaseAuth.signInWithEmailAndPassword(email,password)
            .addOnSuccessListener {
                ingresando()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this,"Falló el inicio sesion debido a ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun ingresando(){
        progressDialog.dismiss()
        startActivity(Intent(this,MainActivityVendedor::class.java))
        finishAffinity()
        Toast.makeText(this,"Bienvenido(a)", Toast.LENGTH_SHORT).show()
    }

    private fun showError(field: EditText, message: String): Boolean {
        field.error = message
        field.requestFocus()
        return false
    }
}