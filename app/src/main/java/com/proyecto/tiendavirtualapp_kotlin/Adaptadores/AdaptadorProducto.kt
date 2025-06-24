package com.proyecto.tiendavirtualapp_kotlin.Adaptadores

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.proyecto.tiendavirtualapp_kotlin.Modelos.ModeloProducto
import com.proyecto.tiendavirtualapp_kotlin.R
import com.proyecto.tiendavirtualapp_kotlin.databinding.ItemProductoBinding

class AdaptadorProducto : RecyclerView.Adapter<AdaptadorProducto.HolderProducto> {
    private lateinit var binding: ItemProductoBinding

    private var mContex : Context
    private var productosArrayList: ArrayList<ModeloProducto>

    constructor(mContex: Context, productosArrayList: ArrayList<ModeloProducto>) {
        this.mContex = mContex
        this.productosArrayList = productosArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderProducto {
        binding = ItemProductoBinding.inflate(LayoutInflater.from(mContex),parent,false)
        return HolderProducto(binding.root)
    }

    override fun getItemCount(): Int {
        return productosArrayList.size
    }

    override fun onBindViewHolder(holder: HolderProducto, position: Int) {
        val modeloProducto = productosArrayList[position]
        val nombre = modeloProducto.nombre
        val precio = modeloProducto.precio
        val precioDesc = modeloProducto.precioDesc
        val notaDesc = modeloProducto.notaDesc

        cargarPrimeraImg(modeloProducto, holder)
        holder.item_nombre_p.text = "${nombre}"
        holder.item_precio_p.text = "${precio} ${"USD"}"
        holder.item_precio_p_desc.text = "${precioDesc}"
        holder.item_nota_p.text = "${notaDesc}"
        //si el presio don descuneto y la nota no son campos vacios
        if(precioDesc.isNotEmpty() && notaDesc.isNotEmpty()){
            visualizarDescuento(holder)
        }
    }

    private fun visualizarDescuento(holder: AdaptadorProducto.HolderProducto) {
        val ref = FirebaseDatabase.getInstance().getReference("Productos")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (ds in snapshot.children){
                    val nota_Desc = "${ds.child("notaDesc").value}"
                    val precio_Desc = "${ds.child("precioDesc").value}"

                    if (nota_Desc.isNotEmpty() && precio_Desc.isEmpty()){
                        //Habilitamos las vistas
                        holder.item_nota_p.visibility = View.VISIBLE
                        holder.item_precio_p_desc.visibility = View.VISIBLE

                        //Seteamos la informacion
                        holder.item_nota_p.text = "${nota_Desc}"
                        holder.item_precio_p_desc.text = "${precio_Desc} ${"USD"}"
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun cargarPrimeraImg(modeloProducto: ModeloProducto, holder: AdaptadorProducto.HolderProducto) {
        val idProducto = modeloProducto.id
        val ref = FirebaseDatabase.getInstance().getReference("Productos")
        ref.child(idProducto).child("Imagenes")
            .limitToLast(1)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(ds in snapshot.children){
                        val imagenUrl = "${ds.child("imagenUrl").value}"
                        try {
                            Glide.with(mContex)
                                .load(imagenUrl)
                                .placeholder(R.drawable.item_img_producto)
                                .into(holder.imagenP)
                        }catch (e:Exception){

                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    inner class HolderProducto(itemView : View) : RecyclerView.ViewHolder(itemView){
        var imagenP = binding.imagenP
        var item_nombre_p = binding.itemNombreP
        var item_precio_p = binding.itemPrecioP
        var item_precio_p_desc = binding.itemPresioPDesc
        var item_nota_p = binding.itemNotaP
    }




}