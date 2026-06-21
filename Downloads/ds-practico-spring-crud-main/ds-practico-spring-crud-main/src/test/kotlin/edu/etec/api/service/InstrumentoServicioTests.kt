package edu.etec.api.service

import edu.etec.api.model.Instrumento
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertNull

class
InstrumentoServicioTests {

    val servicio = InstrumentoServicio()

    @Test
    fun `guardar debe agregar instrumento a la lista y retornarlo`() {
        val instrumento = Instrumento(id = 1, nombre = "Guitarra", descripcion = null)
        val resultado = servicio.guardar(instrumento)

        assertEquals(instrumento, resultado)
        assertEquals(1, servicio.listarTodos().size)
    }

    @Test
    fun `listarTodos debe retornar todos los instrumentos guardados`() {
        servicio.guardar(Instrumento(id = 1, nombre = "Guitarra", descripcion = null))
        servicio.guardar(Instrumento(id = 2, nombre = "Piano", descripcion = "De cola"))

        val todos = servicio.listarTodos()
        assertEquals(2, todos.size)
    }

    @Test
    fun `buscarPorId debe retornar instrumento cuando existe`() {
        val instrumento = Instrumento(id = 1, nombre = "Guitarra", descripcion = null)
        servicio.guardar(instrumento)

        val encontrado = servicio.buscarPorId(1)
        assertTrue(encontrado != null)
        assertEquals("Guitarra", encontrado?.nombre)
    }

    @Test
    fun `buscarPorId debe retornar null cuando no existe`() {
        val encontrado = servicio.buscarPorId(999)
        assertNull(encontrado)
    }

    @Test
    fun `actualizar debe modificar instrumento existente`() {
        val original = Instrumento(id = 1, nombre = "Guitarra", descripcion = null)
        servicio.guardar(original)

        val actualizado = Instrumento(id = 1, nombre = "Guitarra Acustica", descripcion = " Fender")
        val resultado = servicio.actualizar(1, actualizado)

        assertTrue(resultado != null)
        assertEquals("Guitarra Acustica", resultado?.nombre)
        assertEquals(" Fender", resultado?.descripcion)
    }

    @Test
    fun `actualizar debe retornar null cuando instrumento no existe`() {
        val resultado = servicio.actualizar(999, Instrumento(id = 999, nombre = "Test", descripcion = null))
        assertNull(resultado)
    }

    @Test
    fun `eliminar debe remover instrumento de la lista`() {
        val instrumento = Instrumento(id = 1, nombre = "Guitarra", descripcion = null)
        servicio.guardar(instrumento)

        val resultado = servicio.eliminar(1)
        assertTrue(resultado)
        assertEquals(0, servicio.listarTodos().size)
    }

    @Test
    fun `eliminar debe retornar false cuando instrumento no existe`() {
        val resultado = servicio.eliminar(999)
        assertTrue(!resultado)
    }
}