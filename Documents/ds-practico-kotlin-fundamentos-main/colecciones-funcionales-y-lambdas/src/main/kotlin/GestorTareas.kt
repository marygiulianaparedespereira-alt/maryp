/**
 * Ejercicio 2: Find, Any y All
 *
 * Implementa los métodos de esta clase para que pasen todos los tests
 * del archivo Ejercicio2FindAnyAllTest.kt
 *
 * IMPORTANTE: No modifiques la firma de los métodos, solo implementa su lógica.
 */

data class Tarea(
    val id: Int,
    val titulo: String,
    val prioridad: Int, // 1 = baja, 2 = media, 3 = alta
    val completada: Boolean,
    val etiquetas: List<String>,
    val tiempoEstimadoHoras: Int,
)

data class EstadoProyecto(
    val hayTareasCriticasPendientes: Boolean,
    val totalHorasPendientes: Int,
    val todosLosBugsResueltos: Boolean
)

class GestorTareas {
    fun encontrarPrimeraTareaUrgente(tareas: List<Tarea>): Tarea? {
            // Busca el primer elemento con prioridad igual a 3
            return tareas.find { it.prioridad == 3 }
        }

        fun buscarPorId(tareas: List<Tarea>, id: Int): Tarea? {
            // Busca el elemento cuyo id sea idéntico al parámetro
            return tareas.find { it.id == id }
        }

        fun encontrarTareaPendienteConEtiqueta(tareas: List<Tarea>, etiqueta: String): Tarea? {
            // Busca la primera que NO esté completada Y que tenga la etiqueta en su lista
            return tareas.find { !it.completada && it.etiquetas.contains(etiqueta) }
        }

        // Parte B: Operaciones con Any

        fun hayTareasUrgentesPendientes(tareas: List<Tarea>): Boolean {
            // ¿Hay alguna de prioridad 3 que falte terminar?
            return tareas.any { it.prioridad == 3 && !it.completada }
        }

        fun hayTareasQueSuperanHoras(tareas: List<Tarea>, horasLimite: Int): Boolean {
            // ¿Alguna se pasa del límite de horas?
            return tareas.any { it.tiempoEstimadoHoras > horasLimite }
        }

        fun existeTareaConEtiqueta(tareas: List<Tarea>, etiqueta: String): Boolean {
            // ¿Alguna tarea incluye esa etiqueta?
            return tareas.any { it.etiquetas.contains(etiqueta) }
        }

        // Parte C: Operaciones con All

        fun todasCompletadas(tareas: List<Tarea>): Boolean {
            // ¿Están absolutamente todas listas?
            return tareas.all { it.completada }
        }

        fun todasTienenEtiquetas(tareas: List<Tarea>): Boolean {
            // ¿Todas las tareas tienen al menos una etiqueta en su lista?
            return tareas.all { it.etiquetas.isNotEmpty() }
        }

        fun todasDentroDeHoras(tareas: List<Tarea>, horasMaximo: Int): Boolean {
            // ¿Todas cumplen con no pasarse del máximo?
            return tareas.all { it.tiempoEstimadoHoras <= horasMaximo }
        }

        // Parte D: Combinación de Find, Any y All

        fun proyectoListoParaEntrega(tareas: List<Tarea>): Boolean {
            // 1. Todas las de prioridad alta (3) tienen que estar completadas
            val altasCompletadas = tareas.filter { it.prioridad == 3 }.all { it.completada }

            // 2. No tiene que haber NINGUNA tarea pendiente que sea un "blocker"
            val sinBlockersPendientes = !tareas.any { !it.completada && it.etiquetas.contains("blocker") }

            // 3. Al menos una tarea de documentación ("docs") tiene que estar completada
            val tieneDocsTerminado = tareas.any { it.completada && it.etiquetas.contains("docs") }

            return altasCompletadas && sinBlockersPendientes && tieneDocsTerminado
        }

        fun generarResumenEstado(tareas: List<Tarea>): EstadoProyecto {
            // ¿Hay alguna de prioridad 3 sin terminar?
            val criticas = tareas.any { it.prioridad == 3 && !it.completada }

            // Sumamos las horas solo de las tareas que no están completadas
            val horas = tareas.filter { !it.completada }.sumOf { it.tiempoEstimadoHoras }

            // ¿Todas las tareas que tienen la etiqueta "bug" están completas?
            val bugsResueltos = tareas.filter { it.etiquetas.contains("bug") }.all { it.completada }

            return EstadoProyecto(
                hayTareasCriticasPendientes = criticas,
                totalHorasPendientes = horas,
                todosLosBugsResueltos = bugsResueltos
            )
        }
    }