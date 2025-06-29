package com.proyecto.tiendavirtualapp_kotlin.Cliente

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.proyecto.tiendavirtualapp_kotlin.Cliente.Bottom_Nav_Fragments_Cliente.FragmentMisOrdenesC
import com.proyecto.tiendavirtualapp_kotlin.Cliente.Bottom_Nav_Fragments_Cliente.FragmentTiendaC
import com.proyecto.tiendavirtualapp_kotlin.Cliente.Nav_Fragments_Cliente.FragmentInicioC
import com.proyecto.tiendavirtualapp_kotlin.Cliente.Nav_Fragments_Cliente.FragmentMiPerfilC
import com.proyecto.tiendavirtualapp_kotlin.R
import com.proyecto.tiendavirtualapp_kotlin.SeleccionarTipoActivity
import com.proyecto.tiendavirtualapp_kotlin.databinding.ActivityMainClienteBinding


class MainActivityCliente : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainClienteBinding
    private var firebaseAuth : FirebaseAuth?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainClienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        binding.navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            toolbar,
            R.string.open_drawer,
            R.string.close_drawer
        )

        firebaseAuth = FirebaseAuth.getInstance()
        comprobarSesion()
        cargarNombreUsuario()

        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        repleaceFragment(FragmentInicioC())
    }

    private fun cargarNombreUsuario() {
        val uid = firebaseAuth?.uid
        if (uid != null) {
            val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
            ref.child(uid).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val nombres = "${snapshot.child("nombres").value}"
                        val primerNombre = if (nombres.isNotEmpty() && nombres != "null") {
                            nombres.split(" ").firstOrNull() ?: ""
                        } else {
                            ""
                        }

                        val headerView = binding.navigationView.getHeaderView(0)
                        val tvBienvenida = headerView.findViewById<TextView>(R.id.tv_bienvenida_usuario)

                        if (primerNombre.isNotEmpty()) {
                            tvBienvenida.text = "Bienvenido(a) $primerNombre"
                        } else {
                            tvBienvenida.text = "Bienvenido(a)"
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    val headerView = binding.navigationView.getHeaderView(0)
                    val tvBienvenida = headerView.findViewById<TextView>(R.id.tv_bienvenida_usuario)
                    tvBienvenida.text = "Bienvenido(a)"
                }
            })
        }
    }

    private fun comprobarSesion(){
        if(firebaseAuth!!.currentUser == null){
            startActivity(Intent(this@MainActivityCliente, SeleccionarTipoActivity::class.java))
            finishAffinity()
        }else{
            Toast.makeText(this,"Usuario en linea",Toast.LENGTH_SHORT).show()
        }
    }
    private fun cerrarSesion(){
        firebaseAuth!!.signOut()
        startActivity(Intent(this@MainActivityCliente,SeleccionarTipoActivity::class.java))
        finishAffinity()
        Toast.makeText(this,"Cerraste sesion ",Toast.LENGTH_SHORT).show()
    }

    private fun repleaceFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.navFragment,fragment)
            .commit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.op_inicio_c ->{
                repleaceFragment(FragmentInicioC())
            }
            R.id.op_mi_perfil_c ->{
                repleaceFragment(FragmentMiPerfilC())
            }
            R.id.op_cerrar_sesion_c ->{
                cerrarSesion()
            }
            R.id.op_tienda_c ->{
                repleaceFragment(FragmentTiendaC())
            }
            R.id.op_mis_ordenes_c ->{
                repleaceFragment(FragmentMisOrdenesC())
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}
