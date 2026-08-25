package com.flujocicd.backend.servicio;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class AdjuntoServicioTest {

    private final AdjuntoServicio adjuntoServicio = new AdjuntoServicio();
    private final Path directorio = Path.of(AdjuntoServicio.DIRECTORIO_ADJUNTOS);
    private final Path archivo = directorio.resolve("prueba.txt");

    @BeforeEach
    void crearAdjuntoDePrueba() throws IOException {
        Files.createDirectories(directorio);
        Files.writeString(archivo, "contenido de prueba", StandardCharsets.UTF_8);
    }

    @AfterEach
    void limpiarAdjuntoDePrueba() throws IOException {
        Files.deleteIfExists(archivo);
        Files.deleteIfExists(directorio);
    }

    @Test
    void leerAdjuntoDevuelveElContenidoDelArchivo() throws IOException {
        byte[] contenido = adjuntoServicio.leerAdjunto("prueba.txt");

        assertThat(new String(contenido, StandardCharsets.UTF_8)).isEqualTo("contenido de prueba");
    }
}
