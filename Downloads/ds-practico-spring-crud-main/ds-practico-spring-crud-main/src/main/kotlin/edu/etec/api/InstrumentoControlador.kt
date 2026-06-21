package controller

import edu.etec.api.model.Instrumento
import edu.etec.api.service.InstrumentoServicio
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/instrumentos")
class InstrumentoControlador @Autowired constructor(
    private val servicio: InstrumentoServicio
) {

    @GetMapping
    fun listarTodos(): ResponseEntity<List<Instrumento>> {
        return ResponseEntity.ok(servicio.listarTodos())
    }

    @GetMapping("/{id}")
    fun buscarPorId(@PathVariable id: Long): ResponseEntity<Instrumento> {
        val instrumento = servicio.buscarPorId(id) ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        return ResponseEntity.ok(instrumento)
    }

    @PostMapping
    fun guardar(@RequestBody instrumento: Instrumento): ResponseEntity<Any> {
        if (instrumento.nombre.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El nombre no puede estar vacío")
        }
        val nuevo = servicio.guardar(instrumento)
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo)
    }

    @PutMapping("/{id}")
    fun actualizar(@PathVariable id: Long, @RequestBody instrumento: Instrumento): ResponseEntity<Any> {
        if (instrumento.nombre.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El nombre no puede estar vacío")
        }
        val actualizado = servicio.actualizar(id, instrumento) ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        return ResponseEntity.ok(actualizado)
    }

    @DeleteMapping("/{id}")
    fun eliminar(@PathVariable id: Long): ResponseEntity<Void> {
        val eliminado = servicio.eliminar(id)
        if (!eliminado) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }
}