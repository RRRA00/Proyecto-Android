package com.proyecto.tiendavirtualapp_kotlin.Modelos

import android.net.Uri

class ModeloImagenSeleccionada {
    var id = ""
    var imageUri : Uri?= null
    var imagenUri : String ?= null
    var deInternet = false

    constructor()
    constructor(id: String, imageUri: Uri?, imagenUri: String?, deInternet: Boolean) {
        this.id = id
        this.imageUri = imageUri
        this.imagenUri = imagenUri
        this.deInternet = deInternet
    }


}