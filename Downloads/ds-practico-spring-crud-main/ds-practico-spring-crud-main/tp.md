# Trabajo Practico: API REST CRUD con Spring Boot y Kotlin

## Objetivo

Construir una API REST completa para gestionar un **Inventario de Instrumentos Musicales** utilizando Spring Boot y Kotlin, siguiendo un enfoque guiado por tests (TDD).

Al finalizar este TP, el estudiante sera capaz de:
- Comprender el uso de **anotaciones** en Kotlin para Spring
- Implementar el patron **Null Safety** con tipos nullable (`?`)
- Crear endpoints RESTful para operaciones CRUD
- Utilizar MockMvc para testing de controladores

---

## Dominio: Inventario de Instrumentos Musicales

Se gestionara una unica entidad: **Instrumento** con los siguientes campos:
- `id`: Long (obligatorio, auto-generado)
- `nombre`: String (obligatorio)
- `descripcion`: String? (opcional)

---

## Seccion 0: Capsule Teorica

### 0.1 Anotaciones en Kotlin y Spring

Las **anotaciones** son metadatos que se agregan al codigo para proporcionar informacion adicional al compilador o al framework. En Spring, las anotaciones le dicen al framework como gestionar las clases.

**Sintaxis:**
```kotlin
@NombreDeAnotacion
class MiClase
```

**Anotaciones fundamentales de Spring:**

| Anotacion | Proposito |
|-----------|-----------|
| `@SpringBootApplication` | Marca la clase principal de la aplicacion |
| `@RestController` | Marca una clase como controlador REST |
| `@GetMapping` | Mapea solicitudes GET a un metodo |
| `@PostMapping` | Mapea solicitudes POST a un metodo |
| `@PutMapping` | Mapea solicitudes PUT a un metodo |
| `@DeleteMapping` | Mapea solicitudes DELETE a un metodo |
| `@RequestBody` | Indica que el parametro viene del cuerpo de la solicitud |
| `@PathVariable` | Indica que el parametro viene de la URL |
| `@Service` | Marca una clase como servicio de negocio |

**Ejemplo de uso:**
```kotlin
@RestController
class InstrumentoControlador {

    @GetMapping("/instrumentos")
    fun listarTodos(): List<Instrumento> {
        return listOf
    }
}
```

---

### 0.2 Null Safety en Kotlin

Kotlin tiene un sistema de **Null Safety** integrado en el tipo de datos. Por defecto, ninguna variable puede ser `null`. Para permitir valores nulos, se usa el simbolo `?`.

**Tipos nullable:**
```kotlin
// Esta variable puede contener un String o null
var descripcion: String? = null

// Esta variable SIEMPRE tendra un String (nunca null)
var nombre: String = "Guitarra"
```

**Acceso seguro con `?`:**
```kotlin
val descripcion: String? = null

// Acceso seguro: retorna null en lugar de lanzar excepcion
val longitud = descripcion?.length

// Operador Elvis: valor por defecto si es null
val longitud = descripcion?.length ?: 0
```

**En funciones:**
```kotlin
// Esta funcion puede retornar null
fun buscarPorId(id: Long): Instrumento? {
    return instrumentos.find { it.id == id }
}
```

---

## Seccion 1: El Modelo

### Requerimiento

Crear la clase de dominio `Instrumento` que represente un instrumento musical.

**Reglas:**
- `id`: tipo `Long`, obligatorio
- `nombre`: tipo `String`, obligatorio
- `descripcion`: tipo `String?` (nullable), opcional

### Test Provisto

**Archivo:** `src/test/kotlin/edu/etec/api/model/InstrumentoTests.kt`

```kotlin
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
```

### Pista de Implementacion

Usa la palabra clave `data` para crear una **data class**. Esto te da automaticamente:
- `equals()`, `hashCode()`, `toString()`
- `copy()`
- Destructuring

```kotlin
package edu.etec.api.model

data class Instrumento(
    val id: Long,
    val nombre: String,
    val descripcion: String?
)
```

> **Nota:** La clase va en `src/main/kotlin/edu/etec/api/model/Instrumento.kt`

---

## Seccion 2: El Servicio (Logica de Negocio)

### Requerimiento

Crear la clase `InstrumentoServicio` que gestione una lista en memoria (`MutableList`) con las operaciones CRUD.

**Operaciones requeridas:**
- `guardar(instrumento: Instrumento): Instrumento` - Agrega un instrumento
- `listarTodos(): List<Instrumento>` - Lista todos los instrumentos
- `buscarPorId(id: Long): Instrumento?` - Busca por ID (nullable!)
- `actualizar(id: Long, instrumento: Instrumento): Instrumento?` - Actualiza (nullable!)
- `eliminar(id: Long): Boolean` - Elimina (retorna true/false)

### Test Provisto

**Archivo:** `src/test/kotlin/edu/etec/api/service/InstrumentoServicioTests.kt`

```kotlin
package edu.etec.api.service

import edu.etec.api.model.Instrumento
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertNull

class InstrumentoServicioTests {

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
```

### Pista de Implementacion

- Usa `@Service` para que Spring pueda inyectar este bean automaticamente
- La lista en memoria debe ser un `MutableList<Instrumento>` privado
- Para buscar un instrumento por id, usa `find { it.id == id }` que retorna `Instrumento?`
- Para actualizar, puedes usar `indexOfFirst` para encontrar el indice y luego `set()`

```kotlin
package edu.etec.api.service

import edu.etec.api.model.Instrumento
import org.springframework.stereotype.Service

@Service
class InstrumentoServicio {

    private val instrumentos = mutableListOf<Instrumento>()
    private var nextId: Long = 1

    fun guardar(instrumento: Instrumento): Instrumento {
        val nuevoInstrumento = instrumento.copy(id = nextId++)
        instrumentos.add(nuevoInstrumento)
        return nuevoInstrumento
    }

    fun listarTodos(): List<Instrumento> = instrumentos.toList()

    fun buscarPorId(id: Long): Instrumento? {
        return instrumentos.find { it.id == id }
    }

    fun actualizar(id: Long, instrumento: Instrumento): Instrumento? {
        val indice = instrumentos.indexOfFirst { it.id == id }
        if (indice == -1) return null

        val actualizado = instrumento.copy(id = id)
        instrumentos[indice] = actualizado
        return actualizado
    }

    fun eliminar(id: Long): Boolean {
        val removido = instrumentos.removeIf { it.id == id }
        return removido
    }
}
```

> **Nota:** La clase va en `src/main/kotlin/edu/etec/api/service/InstrumentoServicio.kt`

---

## Seccion 3: El Controlador (API REST)

### Requerimiento

Crear el controlador REST `InstrumentoControlador` con los siguientes endpoints:

| Metodo | Endpoint | Descripcion | Estado de exito |
|--------|----------|-------------|-----------------|
| GET | `/api/instrumentos` | Lista todos los instrumentos | 200 OK |
| GET | `/api/instrumentos/{id}` | Obtiene uno por ID | 200 OK / 404 Not Found |
| POST | `/api/instrumentos` | Crea un nuevo instrumento | 201 Created |
| PUT | `/api/instrumentos/{id}` | Actualiza un instrumento | 200 OK / 404 Not Found |
| DELETE | `/api/instrumentos/{id}` | Elimina un instrumento | 204 No Content / 404 Not Found |

**Validacion:** El campo `nombre` no puede estar vacio en POST y PUT.

### Test Provisto

**Archivo:** `src/test/kotlin/edu/etec/api/controller/InstrumentoControladorTests.kt`

```kotlin
package edu.etec.api.controller

import com.fasterxml.jackson.databind.ObjectMapper
import edu.etec.api.model.Instrumento
import edu.etec.api.service.InstrumentoServicio
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@WebMvcTest(InstrumentoControlador::class)
class InstrumentoControladorTests {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockBean
    lateinit var instrumentoServicio: InstrumentoServicio

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Test
    fun `GET todos los instrumentos debe retornar lista vacia inicialmente`() {
        Mockito.`when`(instrumentoServicio.listarTodos()).thenReturn(emptyList())

        mockMvc.perform(MockMvcRequestBuilders.get("/api/instrumentos"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$").isArray)
            .andExpect(MockMvcResultMatchers.jsonPath("$").isEmpty)
    }

    @Test
    fun `GET todos los instrumentos debe retornar instrumentos existentes`() {
        val instrumentos = listOf(
            Instrumento(id = 1, nombre = "Guitarra", descripcion = null),
            Instrumento(id = 2, nombre = "Piano", descripcion = "De cola")
        )
        Mockito.`when`(instrumentoServicio.listarTodos()).thenReturn(instrumentos)

        mockMvc.perform(MockMvcRequestBuilders.get("/api/instrumentos"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$").isArray)
            .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].nombre").value("Guitarra"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].nombre").value("Piano"))
    }

    @Test
    fun `GET instrumento por id debe retornar instrumento cuando existe`() {
        val instrumento = Instrumento(id = 1, nombre = "Guitarra", descripcion = null)
        Mockito.`when`(instrumentoServicio.buscarPorId(1)).thenReturn(instrumento)

        mockMvc.perform(MockMvcRequestBuilders.get("/api/instrumentos/1"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.nombre").value("Guitarra"))
    }

    @Test
    fun `GET instrumento por id debe retornar 404 cuando no existe`() {
        Mockito.`when`(instrumentoServicio.buscarPorId(999)).thenReturn(null)

        mockMvc.perform(MockMvcRequestBuilders.get("/api/instrumentos/999"))
            .andExpect(MockMvcResultMatchers.status().isNotFound)
    }

    @Test
    fun `POST debe crear nuevo instrumento y retornarlo`() {
        val nuevoInstrumento = Instrumento(id = 1, nombre = "Guitarra", descripcion = null)
        Mockito.`when`(instrumentoServicio.guardar(Mockito.any(Instrumento::class.java))).thenReturn(nuevoInstrumento)

        mockMvc.perform(MockMvcRequestBuilders.post("/api/instrumentos")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(nuevoInstrumento)))
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.jsonPath("$.nombre").value("Guitarra"))
    }

    @Test
    fun `POST debe retornar 400 cuando nombre esta vacio`() {
        val instrumentoInvalido = Instrumento(id = 0, nombre = "", descripcion = null)

        mockMvc.perform(MockMvcRequestBuilders.post("/api/instrumentos")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(instrumentoInvalido)))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun `PUT debe actualizar instrumento existente`() {
        val instrumentoActualizado = Instrumento(id = 1, nombre = "Guitarra Acustica", descripcion = "Fender")
        Mockito.`when`(instrumentoServicio.actualizar(Mockito.eq(1), Mockito.any(Instrumento::class.java)))
            .thenReturn(instrumentoActualizado)

        mockMvc.perform(MockMvcRequestBuilders.put("/api/instrumentos/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(instrumentoActualizado)))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.nombre").value("Guitarra Acustica"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.descripcion").value("Fender"))
    }

    @Test
    fun `PUT debe retornar 404 cuando instrumento no existe`() {
        val instrumentoActualizado = Instrumento(id = 999, nombre = "Test", descripcion = null)
        Mockito.`when`(instrumentoServicio.actualizar(Mockito.eq(999), Mockito.any(Instrumento::class.java)))
            .thenReturn(null)

        mockMvc.perform(MockMvcRequestBuilders.put("/api/instrumentos/999")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(instrumentoActualizado)))
            .andExpect(MockMvcResultMatchers.status().isNotFound)
    }

    @Test
    fun `DELETE debe remover instrumento y retornar 204`() {
        Mockito.`when`(instrumentoServicio.eliminar(1)).thenReturn(true)

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/instrumentos/1"))
            .andExpect(MockMvcResultMatchers.status().isNoContent)
    }

    @Test
    fun `DELETE debe retornar 404 cuando instrumento no existe`() {
        Mockito.`when`(instrumentoServicio.eliminar(999)).thenReturn(false)

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/instrumentos/999"))
            .andExpect(MockMvcResultMatchers.status().isNotFound)
    }
}
```

### Pista de Implementacion

- Usa `@RestController` y `@RequestMapping("/api/instrumentos")`
- Inyecta el servicio con `@Autowired` (o mejor, mediante el constructor)
- `buscarPorId` y similares retornan `Instrumento?`, usa `if (instrumento == null)` para el 404
- Para validar, puedes crear un DTO de request o validar en el controlador mismo

```kotlin
package edu.etec.api.controller

import edu.etec.api.model.Instrumento
import edu.etec.api.service.InstrumentoServicio
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/instrumentos")
class InstrumentoControlador(
    @Autowired private val servicio: InstrumentoServicio
) {

    @GetMapping
    fun listarTodos(): List<Instrumento> {
        return servicio.listarTodos()
    }

    @GetMapping("/{id}")
    fun buscarPorId(@PathVariable id: Long): ResponseEntity<Instrumento> {
        val instrumento = servicio.buscarPorId(id)
        return if (instrumento == null) {
            ResponseEntity.notFound().build()
        } else {
            ResponseEntity.ok(instrumento)
        }
    }

    @PostMapping
    fun crear(@RequestBody instrumento: Instrumento): ResponseEntity<Any> {
        if (instrumento.nombre.isBlank()) {
            return ResponseEntity.badRequest().build()
        }
        val guardado = servicio.guardar(instrumento)
        return ResponseEntity.status(201).body(guardado)
    }

    @PutMapping("/{id}")
    fun actualizar(@PathVariable id: Long, @RequestBody instrumento: Instrumento): ResponseEntity<Any> {
        if (instrumento.nombre.isBlank()) {
            return ResponseEntity.badRequest().build()
        }
        val actualizado = servicio.actualizar(id, instrumento)
        return if (actualizado == null) {
            ResponseEntity.notFound().build()
        } else {
            ResponseEntity.ok(actualizado)
        }
    }

    @DeleteMapping("/{id}")
    fun eliminar(@PathVariable id: Long): ResponseEntity<Any> {
        val eliminado = servicio.eliminar(id)
        return if (!eliminado) {
            ResponseEntity.notFound().build()
        } else {
            ResponseEntity.noContent().build()
        }
    }
}
```

> **Nota:** La clase va en `src/main/kotlin/edu/etec/api/controller/InstrumentoControlador.kt`

---

## Comandos para Ejecutar los Tests

Desde la raiz del proyecto:

```bash
# Ejecutar todos los tests
./mvnw test

# Ejecutar solo los tests de una seccion especifica
./mvnw test -Dtest=InstrumentoTests
./mvnw test -Dtest=InstrumentoServicioTests
./mvnw test -Dtest=InstrumentoControladorTests
```

---

## Criterios de Aprobacion

Para aprobar este TP, debes:

1. **Seccion 1:** Los tests de `InstrumentoTests` pasan (verde)
2. **Seccion 2:** Los tests de `InstrumentoServicioTests` pasan (verde)
3. **Seccion 3:** Los tests de `InstrumentoControladorTests` pasan (verde)

Todos los tests deben estar en verde para considerar el TP como aprobado.

---

## Estructura de Archivos Esperada

```
crud-basico/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── kotlin/edu/etec/api/
│   │   │   ├── ApiApplication.kt
│   │   │   ├── model/
│   │   │   │   └── Instrumento.kt
│   │   │   ├── service/
│   │   │   │   └── InstrumentoServicio.kt
│   │   │   └── controller/
│   │   │       └── InstrumentoControlador.kt
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── kotlin/edu/etec/api/
│           ├── ApiApplicationTests.kt
│           ├── model/
│           │   └── InstrumentoTests.kt
│           ├── service/
│           │   └── InstrumentoServicioTests.kt
│           └── controller/
│               └── InstrumentoControladorTests.kt
└── tp.md (este documento)
```

---

## Recursos Adicionales

- [Kotlin Null Safety](https://kotlinlang.org/docs/null-safety.html)
- [Spring Boot Annotations](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-annotations)
- [MockMvc Testing](https://spring.io/guides/gs/testing-web/)