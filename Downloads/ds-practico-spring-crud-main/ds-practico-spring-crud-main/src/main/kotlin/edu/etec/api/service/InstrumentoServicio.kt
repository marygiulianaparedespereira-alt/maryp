package edu.etec.api.service

import edu.etec.api.model.Instrumento
import org.springframework.stereotype.Service

@Service
class InstrumentoServicio {

    private val instrumentos = mutableListOf<Instrumento>()

    fun listarTodos(): List<Instrumento> {
        return instrumentos
    }

    fun buscarPorId(id: Long): Instrumento? {
        return instrumentos.find { it.id == id }
    }

    fun guardar(instrumento: Instrumento): Instrumento {
        instrumentos.add(instrumento)
        return instrumento
    }

    fun actualizar(id: Long, instrumentoActualizado: Instrumento): Instrumento? {
        val indice = instrumentos.indexOfFirst { it.id == id }
        if (indice == -1) return null

        instrumentos[indice] = instrumentoActualizado
        return instrumentoActualizado
    }

    fun eliminar(id: Long): Boolean {
        return instrumentos.removeIf { it.id == id }
    }
}