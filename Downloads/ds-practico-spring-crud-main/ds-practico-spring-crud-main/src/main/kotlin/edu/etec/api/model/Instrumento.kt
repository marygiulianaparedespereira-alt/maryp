

package edu.etec.api.model

data class Instrumento(
    val id: Long,
    val nombre: String,
    val descripcion: String? = null
)