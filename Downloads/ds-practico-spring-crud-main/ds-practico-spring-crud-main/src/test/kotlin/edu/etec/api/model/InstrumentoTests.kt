package edu.etec.api.model

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals

class InstrumentoTests {

    @Test
    fun `instrumento debe tener id nombre y descripcion opcional`() {
        val instrumento = Instrumento(
            id = 1,
            nombre = "Guitarra",
            descripcion = null
        )
        assertEquals(1, instrumento.id)
        assertEquals("Guitarra", instrumento.nombre)
        assertEquals(null, instrumento.descripcion)
    }

    @Test
    fun `instrumento con descripcion debe almacenarla correctamente`() {
        val instrumento = Instrumento(
            id = 2,
            nombre = "Piano",
            descripcion = "Piano de cola Steinway"
        )
        assertEquals(2, instrumento.id)
        assertEquals("Piano", instrumento.nombre)
        assertEquals("Piano de cola Steinway", instrumento.descripcion)
    }
}