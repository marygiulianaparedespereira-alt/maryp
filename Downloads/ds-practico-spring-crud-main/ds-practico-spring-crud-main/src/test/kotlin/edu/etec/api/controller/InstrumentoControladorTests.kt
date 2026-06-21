package edu.etec.api.controller

import edu.etec.api.model.Instrumento
import edu.etec.api.service.InstrumentoServicio
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@WebMvcTest(InstrumentoControlador::class)
class InstrumentoControladorTests {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockBean
    lateinit var instrumentoServicio: InstrumentoServicio

    @Test
    fun `GET debe retornar lista vacia al inicio`() {
        Mockito.`when`(instrumentoServicio.listarTodos()).thenReturn(emptyList())

        mockMvc.perform(MockMvcRequestBuilders.get("/api/instrumentos"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().json("[]"))
    }
}