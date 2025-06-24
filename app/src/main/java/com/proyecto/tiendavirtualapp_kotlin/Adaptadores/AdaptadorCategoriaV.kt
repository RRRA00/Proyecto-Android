package com.proyecto.tiendavirtualapp_kotlin.Adaptadores

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.proyecto.tiendavirtualapp_kotlin.Modelos.ModeloCategoria
import com.proyecto.tiendavirtualapp_kotlin.databinding.ItemCategoriaVBinding

class AdaptadorCategoriaV: RecyclerView.Adapter<AdaptadorCategoriaV.HolderCategotiaV> {
    private lateinit var binding : ItemCategoriaVBinding

    private val mContext : Context
    private val categoriaArrayList : ArrayList<ModeloCategoria>

    constructor(mContext: Context, categoriaArray: ArrayList<ModeloCategoria>) {
        this.mContext = mContext
        this.categoriaArrayList = categoriaArray
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderCategotiaV {
        binding = ItemCategoriaVBinding.inflate(LayoutInflater.from(mContext),parent,false)
        return HolderCategotiaV(binding.root)
    }

    override fun getItemCount(): Int {
        return categoriaArrayList.size
    }

    override fun onBindViewHolder(holder: HolderCategotiaV, position: Int) {
        val modelo = categoriaArrayList[position]

        val id = modelo.id
        val categoria = modelo.categoria

        holder.item_nombre_c_v.text = categoria
        holder.item_eliminar_c.setOnClickListener{
            //Toast.makeText(mContext, "Eliminar categoria", Toast.LENGTH_SHORT).show()
            val builder = AlertDialog.Builder(mContext)
            builder.setTitle("Eliminar categoria")
            builder.setMessage("¿Estas seguro de eliminar esta categoria?")
                .setPositiveButton("Confirmar"){a,d->
                    eliminarCategoria(modelo, holder)
                }
                .setNegativeButton("Cancelar"){a,b->
                    a.dismiss()
                }
            builder.show()
        }
    }

    private fun eliminarCategoria(modelo: ModeloCategoria, holder: AdaptadorCategoriaV.HolderCategotiaV) {
        val idCat =  modelo.id
        val ref = FirebaseDatabase.getInstance().getReference("Categorias")
        ref.child(idCat).removeValue()
            .addOnSuccessListener {
                Toast.makeText(mContext, "Categoria eliminada", Toast.LENGTH_SHORT).show()
                eliminarImagenCat(idCat)
            }
            .addOnFailureListener {e->
                Toast.makeText(mContext, "Nose elimino la categoria devido a ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun eliminarImagenCat(idCat: String) {
        val nombreImg = idCat
        val rutaImg = "Categorias/$nombreImg"
        val storageRef = FirebaseStorage.getInstance().getReference(rutaImg)
        storageRef.delete()
            .addOnSuccessListener {
                Toast.makeText(mContext, "Se elimino la img de categoria", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e->
                Toast.makeText(mContext, "${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    inner class HolderCategotiaV(itemView : View) : RecyclerView.ViewHolder(itemView){
        var item_nombre_c_v = binding.itemNombreCV
        var item_eliminar_c = binding.itemEliminarC
    }


}