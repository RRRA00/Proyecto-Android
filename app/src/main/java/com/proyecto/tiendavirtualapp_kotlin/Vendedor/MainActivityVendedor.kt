package com.proyecto.tiendavirtualapp_kotlin.Vendedor

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast

import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.proyecto.tiendavirtualapp_kotlin.R
import com.proyecto.tiendavirtualapp_kotlin.SeleccionarTipoActivity
import com.proyecto.tiendavirtualapp_kotlin.Vendedor.Bottom_Nav_Fragments_Vendedor.FragmentMisProductosV
import com.proyecto.tiendavirtualapp_kotlin.Vendedor.Bottom_Nav_Fragments_Vendedor.FragmentOrdendesV
import com.proyecto.tiendavirtualapp_kotlin.Vendedor.Nav_Fragment_Vendedor.FragmentCategoriasV
import com.proyecto.tiendavirtualapp_kotlin.Vendedor.Nav_Fragment_Vendedor.FragmentInicioV
import com.proyecto.tiendavirtualapp_kotlin.Vendedor.Nav_Fragment_Vendedor.FragmentMiTiendaV
import com.proyecto.tiendavirtualapp_kotlin.Vendedor.Nav_Fragment_Vendedor.FragmentReseniasV
import com.proyecto.tiendavirtualapp_kotlin.databinding.ActivityMainVendedorBinding

class MainActivityVendedor : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainVendedorBinding
    private var firebaseAuth : FirebaseAuth?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainVendedorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        firebaseAuth = FirebaseAuth.getInstance()
        comprobarSesion()

        binding.navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            toolbar,
            R.string.open_drawer,
            R.string.close_drawer
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()


        replaceFragment(FragmentInicioV())
        binding.navigationView.setCheckedItem(R.id.op_inicio_v)
    }
    private fun cerrarSecion(){
        firebaseAuth!!.signOut()
        startActivity(Intent(applicationContext,SeleccionarTipoActivity::class.java))
        finish()
        Toast.makeText(applicationContext,"Has cerrado sesion",Toast.LENGTH_SHORT).show()
    }

    private fun comprobarSesion() {
        /*si el ususario no ha iniciado sesion */
        if(firebaseAuth!!.currentUser == null){
            startActivity(Intent(applicationContext,SeleccionarTipoActivity::class.java))
            Toast.makeText(applicationContext,"Vendedor no registrado o no logeado",Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(applicationContext,"Vendedor en linea",Toast.LENGTH_SHORT).show()
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.navFragment,fragment)
            .commit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.op_inicio_v->{
                replaceFragment(FragmentInicioV())
            }
            R.id.op_mi_tienda_v->{
                replaceFragment(FragmentMiTiendaV())
            }
            R.id.op_categorias_v->{
                replaceFragment(FragmentCategoriasV())
            }
            R.id.op_resenia_v->{
                replaceFragment(FragmentReseniasV())
            }
            R.id.op_cerrar_sesion_v->{
                cerrarSecion()
            }
            R.id.op_mis_productos_v->{
                replaceFragment(FragmentMisProductosV())
            }
            R.id.op_mis_productos_v->{
                replaceFragment(FragmentOrdendesV())
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}