/**
 * Ejercicio 5: It y Scope Functions (run, apply, also, let)
 *
 * Implementa los métodos de esta clase para que pasen todos los tests
 * del archivo Ejercicio5ItScopeFunctionsTest.kt
 *
 * IMPORTANTE: No modifiques la firma de los métodos, solo implementa su lógica.
 * IMPORTANTE: Debes usar las scope functions indicadas en cada sección.
 */

data class Usuario(
    var id: Int = 0,
    var nombre: String = "",
    var email: String = "",
    var activo: Boolean = false,
    var roles: MutableList<String> = mutableListOf(),
    var configuracion: ConfiguracionUsuario = ConfiguracionUsuario(),
)

data class ConfiguracionUsuario(
    var tema: String = "claro",
    var idioma: String = "es",
    var notificaciones: Boolean = true,
    var nivelPrivacidad: Int = 1,
)

data class Validacion(
    val campo: String,
    val valido: Boolean,
    val mensaje: String,
)

class UsuarioBuilder {
    // Parte A: Uso del parámetro implícito 'it'

    fun procesarNumeros(numeros: List<Int>): List<Int> {
        // Filtramos usando 'it' para los pares y luego mapeamos multiplicando por 10
        return numeros.filter { it % 2 == 0 }.map { it * 10 }
    }

    fun validarUsuarios(usuarios: List<Usuario>): List<List<Validacion>> {
        return usuarios.map {
            listOf(
                Validacion("nombre", it.nombre.isNotEmpty(), "El nombre no debe estar vacío"),
                Validacion("email", it.email.contains("@"), "El email debe contener '@'"),
                Validacion("roles", it.roles.isNotEmpty(), "Debe tener al menos un rol")
            )
        }
    }

    fun procesarTextos(textos: List<String>): List<String> {
        return textos
            .map { it.trim() }
            .map { it.lowercase() }
            .filter { it.isNotEmpty() }
    }

    // Parte B: Función run

    fun calcularNivelAcceso(usuario: Usuario): Int {
        // 'run' nos permite operar directamente sobre el contexto del usuario y retornar la suma final
        return usuario.run {
            var puntos = 0
            if (activo) puntos += 10
            puntos += roles.size * 5
            if (email.contains("@empresa.com")) puntos += 5
            puntos
        }
    }

    fun crearUsuarioConTipo(tipo: String): Usuario {
        // Creamos un usuario base y usamos 'run' para decidir y aplicar su configuración según el tipo
        val usuario = Usuario()
        return usuario.run {
            when (tipo) {
                "ADMIN" -> {
                    roles = mutableListOf("ADMIN")
                    configuracion.nivelPrivacidad = 3
                    configuracion.notificaciones = true
                }
                "USER" -> {
                    roles = mutableListOf("USER")
                    configuracion.nivelPrivacidad = 1
                    configuracion.notificaciones = false
                }
            }
            this // Retornamos el objeto mutado
        }
    }

    // Parte C: Función apply

    fun crearUsuarioCompleto(
        nombre: String,
        email: String,
        roles: List<String>,
    ): Usuario {
        // 'apply' nos devuelve el mismo objeto modificado (retorna el propio receptor)
        return Usuario().apply {
            this.nombre = nombre
            this.email = email
            this.activo = true
            this.roles = roles.toMutableList()
            this.configuracion = ConfiguracionUsuario()
        }
    }

    fun actualizarUsuario(
        usuario: Usuario,
        actualizacion: Usuario.() -> Unit,
    ): Usuario {
        // Aplicamos la lambda de extensión recibida directamente sobre el usuario usando 'apply'
        return usuario.apply(actualizacion)
    }

    // Parte D: Función also

    fun crearUsuarioConLog(
        nombre: String,
        email: String,
        onLog: (String) -> Unit,
    ): Usuario {
        // 'also' recibe el objeto como 'it' y es excelente para ejecutar efectos secundarios (como logs)
        return Usuario().also { onLog("Usuario creado: $nombre") }
            .apply { this.nombre = nombre; this.email = email }
            .also { onLog("Email asignado: $email") }
            .apply { this.activo = true }
            .also { onLog("Usuario activado") }
    }

    fun crearYValidar(
        nombre: String,
        email: String,
    ): Pair<Usuario, Boolean> {
        val usuario = Usuario(nombre = nombre, email = email)
        var esValido = false
        // Usamos 'also' para inspeccionar y validar las propiedades del usuario creado sin romper el flujo
        usuario.also {
            esValido = it.nombre.isNotEmpty() && it.email.contains("@")
        }
        return Pair(usuario, esValido)
    }

    // Parte E: Función let

    fun procesarEmailOpcional(email: String?): String {
        // 'let' es perfecto para operaciones null-safe combinadas con el operador elvis (?:)
        return email?.let { "Usuario con email: $it" } ?: "Usuario sin email"
    }

    fun generarMensajesBienvenida(usuarios: List<Usuario>): List<String> {
        return usuarios
            .filter { it.activo && it.email.isNotEmpty() }
            .map { usuario ->
                // Usamos 'let' para transformar la estructura del usuario en una cadena de bienvenida
                usuario.let { "Bienvenido/a ${it.nombre} (${it.email})" }
            }
    }

    // Parte F: Combinación de Scope Functions

    fun procesarUsuarioComplejo(datosBase: Map<String, String>): Usuario? {
        val nombre = datosBase["nombre"] ?: return null
        val email = datosBase["email"] ?: return null
        val departamento = datosBase["departamento"]

        // Combinamos run para instanciar, apply para setear, y also para las reglas del departamento de IT
        return Usuario().run {
            apply {
                this.nombre = nombre
                this.email = email
            }
        }.also { usuario ->
            if (departamento == "IT") {
                usuario.roles.add("IT_USER")
                usuario.configuracion.tema = "oscuro"
            }
        }
    }

    fun procesarLoteUsuarios(usuarios: List<Usuario>): List<Usuario> {
        return usuarios.map { usuario ->
            usuario
                .apply { activo = true }
                .also { if (it.roles.isEmpty()) it.roles.add("USER") }
                .apply { configuracion.notificaciones = true }
                .run {
                    if (nombre == "Admin") {
                        roles.add("ADMIN")
                        configuracion.nivelPrivacidad = 3
                    }
                    this // Retornamos el objeto procesado por el pipeline
                }
        }
    }

    fun parsearYCrearUsuario(datosRaw: String): Usuario? {
        return try {
            // Convertimos la cadena cruda en un mapa de claves y valores
            val mapaDatos = datosRaw.split("|")
                .filter { it.contains(":") }
                .associate {
                    val partes = it.split(":")
                    partes[0].trim() to partes[1].trim()
                }

            if (!mapaDatos.containsKey("id") || !mapaDatos.containsKey("nombre")) return null

            Usuario().apply {
                id = mapaDatos["id"]?.toInt() ?: 0
                nombre = mapaDatos["nombre"] ?: ""
                email = mapaDatos["email"] ?: ""
                activo = mapaDatos["activo"]?.toBoolean() ?: false
                mapaDatos["roles"]?.let { rolesStr ->
                    roles = rolesStr.split(",").map { it.trim() }.toMutableList()
                }
                configuracion = ConfiguracionUsuario().apply {
                    tema = mapaDatos["tema"] ?: "claro"
                    idioma = mapaDatos["idioma"] ?: "es"
                }
            }
        } catch (e: Exception) {
            null // Si el parseo falla estrepitosamente, devolvemos null de manera segura
        }
    }
}