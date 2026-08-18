package com.flujocicd.backend.controlador;

import com.flujocicd.backend.dto.TareaRequest;
import com.flujocicd.backend.repositorio.TareaRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TareaControladorTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TareaRepositorio tareaRepositorio;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void limpiarDatos() {
        tareaRepositorio.deleteAll();
    }

    @Test
    void listarDevuelveArregloVacioSinTareas() throws Exception {
        mockMvc.perform(get("/api/tareas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void crearTareaValidaDevuelve201() throws Exception {
        TareaRequest request = new TareaRequest("Comprar dominio", "Para el ambiente de preprod");

        mockMvc.perform(post("/api/tareas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titulo").value("Comprar dominio"))
                .andExpect(jsonPath("$.completada").value(false));
    }

    @Test
    void crearTareaSinTituloDevuelve400() throws Exception {
        TareaRequest request = new TareaRequest("", "sin titulo");

        mockMvc.perform(post("/api/tareas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void obtenerTareaInexistenteDevuelve404() throws Exception {
        mockMvc.perform(get("/api/tareas/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void flujoCompletoCrearCompletarYEliminar() throws Exception {
        TareaRequest request = new TareaRequest("Tarea temporal", "detalle");

        String respuesta = mockMvc.perform(post("/api/tareas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(respuesta).get("id").asLong();

        mockMvc.perform(patch("/api/tareas/{id}/completar", id).param("completada", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completada").value(true));

        mockMvc.perform(delete("/api/tareas/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/tareas/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void buscarPorTituloEncuentraCoincidenciasParciales() throws Exception {
        mockMvc.perform(post("/api/tareas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new TareaRequest("Revisar pipeline CI/CD", null))));

        mockMvc.perform(get("/api/tareas/buscar").param("texto", "pipeline"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(greaterThanOrEqualTo(1)));
    }
}
