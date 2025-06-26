package com.proyecto.tiendavirtualapp_kotlin.Modelos

class ModeloUsuario {
    var uid: String = ""
    var nombres: String = ""
    var email: String = ""
    var telefono: String = ""
    var dni: String = ""
    var proveedor: String = ""
    var tRegistro: String = ""
    var imagen: String = ""
    var tipoUsuario: String = ""

    constructor()

    constructor(
        uid: String,
        nombres: String,
        email: String,
        telefono: String,
        dni: String,
        proveedor: String,
        tRegistro: String,
        imagen: String,
        tipoUsuario: String
    ) {
        this.uid = uid
        this.nombres = nombres
        this.email = email
        this.telefono = telefono
        this.dni = dni
        this.proveedor = proveedor
        this.tRegistro = tRegistro
        this.imagen = imagen
        this.tipoUsuario = tipoUsuario
    }
}
