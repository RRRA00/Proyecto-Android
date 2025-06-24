package com.proyecto.tiendavirtualapp_kotlin.Vendedor.Productos


import android.app.Activity
import android.app.ProgressDialog
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.util.ImageUtil.compressImage
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.proyecto.tiendavirtualapp_kotlin.Adaptadores.AdaptadorImagenSeleccionada
import com.proyecto.tiendavirtualapp_kotlin.Constantes
import com.proyecto.tiendavirtualapp_kotlin.Modelos.ModeloCategoria
import com.proyecto.tiendavirtualapp_kotlin.Modelos.ModeloImagenSeleccionada
import com.proyecto.tiendavirtualapp_kotlin.R
import com.proyecto.tiendavirtualapp_kotlin.databinding.ActivityAgregarProductoBinding
import java.io.File
import java.io.FileOutputStream

class AgregarProductoActivity : AppCompatActivity() {
    private lateinit var binding : ActivityAgregarProductoBinding
    private var imagenUri : Uri?=null

    private lateinit var imagenSelecArrayList : ArrayList<ModeloImagenSeleccionada>
    private lateinit var adaptadorImagenSel : AdaptadorImagenSeleccionada

    private lateinit var categoriasArrayList : ArrayList<ModeloCategoria>

    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgregarProductoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cargarCategorias()
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.etPrecioConDescuentoP.visibility = View.GONE
        binding.etNotaDescuentoP.visibility = View.GONE

        binding.descuentoSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                //switch esta true
                binding.etPrecioConDescuentoP.visibility = View.VISIBLE
                binding.etNotaDescuentoP.visibility = View.VISIBLE
            }else{
                //switch esta false
                binding.etPrecioConDescuentoP.visibility = View.GONE
                binding.etNotaDescuentoP.visibility = View.GONE
            }
        }

        imagenSelecArrayList = ArrayList()

        binding.imgAgregarProducto.setOnClickListener {
            seleccionarImg()
        }
        binding.Categoria.setOnClickListener {
            selecCategorias()
        }
        binding.btnAgregarProducto.setOnClickListener{
            validarInfo()
        }
        cargarImagenes()

    }

    private var nombreP = ""
    private var descripcionP = ""
    private var categoriaP = ""
    private var precioP = ""
    private var descuentoHab = false
    private var presioDescP = ""
    private var notaDescP = ""
    private fun validarInfo() {
        nombreP = binding.etNombresP.text.toString().trim()
        descripcionP = binding.etDescripcionP.text.toString().trim()
        categoriaP = binding.Categoria.text.toString().trim()
        precioP = binding.etPrecioP.text.toString().trim()
        descuentoHab = binding.descuentoSwitch.isChecked
        if(nombreP.isEmpty()){
            binding.etNombresP.error = "Ingrese un nombre"
            binding.etNombresP.requestFocus()
        }else if(descripcionP.isEmpty()){
            binding.etDescripcionP.error = "Ingrese una descripción"
            binding.etDescripcionP.requestFocus()
        }else if(categoriaP.isEmpty()){
            binding.Categoria.error = "Seleccione una Categoria"
            binding.Categoria.requestFocus()
        }else if(precioP.isEmpty()){
            binding.etPrecioP.error = "Ingrese un precio"
            binding.etPrecioP.requestFocus()
        }else if(imagenUri == null){
            Toast.makeText(this, "Seleccione almenos una imagen",Toast.LENGTH_SHORT).show()
        }else{
            //descuentoHab == true
            if(descuentoHab){
                presioDescP = binding.etPrecioConDescuentoP.text.toString().trim()
                notaDescP = binding.etNotaDescuentoP.text.toString().trim()
                if(presioDescP.isEmpty()){
                    binding.etPrecioConDescuentoP.error = "Ingrese presio con desc."
                    binding.etPrecioConDescuentoP.requestFocus()
                }else if(notaDescP.isEmpty()){
                    binding.etNotaDescuentoP.error = "Ingrese nota de desc."
                    binding.etNotaDescuentoP.requestFocus()
                }else{
                    agregarProducto()
                }

            }else{
                presioDescP = "0"
                notaDescP = ""
                agregarProducto()
            }
        }

    }

    private fun agregarProducto() {
        progressDialog.setMessage("Agregando producto")
        progressDialog.show()

        var ref = FirebaseDatabase.getInstance().getReference("Productos")
        val keyId = ref.push().key

        val hashMap = HashMap<String,Any>()
        hashMap["id"] = "${keyId}"
        hashMap["nombre"] = "${nombreP}"
        hashMap["descripcion"] = "${descripcionP}"
        hashMap["categoria"] = "${categoriaP}"
        hashMap["precio"] = "${precioP}"
        hashMap["precioDesc"] = "${presioDescP}"
        hashMap["notaDesc"] = "${notaDescP}"

        ref.child(keyId!!)
            .setValue(hashMap)
            .addOnSuccessListener {
                subirImgsStorage(keyId)


            }
            .addOnFailureListener {e->
                Toast.makeText(this, "${e.message}", Toast.LENGTH_SHORT).show()
            }

    }

    private fun subirImgsStorage(keyId: String){
        for(i in imagenSelecArrayList.indices){
            val modeloImagenSel = imagenSelecArrayList[i]
            val nombreImagen = modeloImagenSel.id
            val rutaImagen = "Productos/$nombreImagen"
            // Comprimir la imagen
            val compressedFile = compressImage(modeloImagenSel.imageUri!!)
            if (compressedFile == null) {
                Toast.makeText(this, "Error al comprimir imagen: $nombreImagen", Toast.LENGTH_SHORT).show()
                continue
            }

            val fileUri = Uri.fromFile(compressedFile)
            val storageRef =FirebaseStorage.getInstance().getReference(rutaImagen)

            storageRef.putFile(fileUri)
                .addOnSuccessListener { taskSnapshot->
                    val uriTask = taskSnapshot.storage.downloadUrl
                    while (!uriTask.isSuccessful);
                    val urlImgCargada = uriTask.result

                    if(uriTask.isSuccessful){
                        val hashMap = HashMap<String,Any>()
                        hashMap["id"] ="${modeloImagenSel.id}"
                        hashMap["imagenUrl"] = "${urlImgCargada}"

                        val ref = FirebaseDatabase.getInstance().getReference("Productos")
                        ref.child(keyId).child("Imagenes")
                            .child(nombreImagen)
                            .updateChildren(hashMap)
                        progressDialog.dismiss()
                        Toast.makeText(this,"Se agrego el producto",Toast.LENGTH_SHORT).show()
                        limpiarCampos()
                    }

                }
        }
    }
    private fun compressImage(uri: Uri): File? {
        return try {
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
            val compressedFile = File.createTempFile("compressed_", ".jpg", cacheDir)
            val outputStream = FileOutputStream(compressedFile)

            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)

            outputStream.flush()
            outputStream.close()

            compressedFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun limpiarCampos() {
        imagenSelecArrayList.clear()
        adaptadorImagenSel.notifyDataSetChanged()
        binding.etNombresP.setText("")
        binding.etDescripcionP.setText("")
        binding.etPrecioP.setText("")
        binding.Categoria.setText("")
        binding.descuentoSwitch.isChecked = false
        binding.etPrecioConDescuentoP.setText("")
        binding.etNotaDescuentoP.setText("")
    }

    private fun cargarCategorias() {
        categoriasArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Categorias").orderByChild("categoria")
        ref.addValueEventListener(object  : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                categoriasArrayList.clear()
                for(ds in snapshot.children){
                    val modelo = ds.getValue(ModeloCategoria::class.java)
                    categoriasArrayList.add(modelo!!)
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
    private var idCat=""
    private var tituloCat = ""
    private fun selecCategorias(){
        val categoriasArray = arrayOfNulls<String>(categoriasArrayList.size)
        for(i in categoriasArray.indices){
            categoriasArray[i] = categoriasArrayList[i].categoria
        }
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Seleccione una categoria")
            .setItems(categoriasArray){dialog, witch->
                idCat = categoriasArrayList[witch].id
                tituloCat = categoriasArrayList[witch].categoria
                binding.Categoria.text = tituloCat
            }
            .show()
    }

    private fun cargarImagenes() {
        adaptadorImagenSel = AdaptadorImagenSeleccionada(this,imagenSelecArrayList)
        binding.RVImagenesProducto.adapter = adaptadorImagenSel
    }

    private fun seleccionarImg(){
        ImagePicker.with(this)
            .crop()
            .compress(1024)
            .maxResultSize(180,180)
            .createIntent { intent->
                resultadoImg.launch(intent)
            }
    }
    private val resultadoImg =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){resultado ->
            if(resultado.resultCode == Activity.RESULT_OK){
                val data = resultado.data
                imagenUri = data!!.data
                val tiempo  = "${Constantes().obtenerTiempoD()}"
                val modeloImgSel = ModeloImagenSeleccionada(tiempo,imagenUri,null,false)
                imagenSelecArrayList.add(modeloImgSel)
                cargarImagenes()
            }else{
                Toast.makeText(this,"Accion cancelada", Toast.LENGTH_SHORT).show()
            }
        }
}