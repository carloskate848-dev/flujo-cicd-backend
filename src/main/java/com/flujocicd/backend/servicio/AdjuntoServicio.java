package com.flujocicd.backend.servicio;

import com.flujocicd.backend.excepcion.AdjuntoInvalidoException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Servicio para leer los adjuntos que el usuario sube a una tarea.
 *
 * El nombre de archivo recibido del usuario se resuelve contra el
 * directorio base y luego se normaliza (Path#normalize) para colapsar
 * secuencias como "..". Si la ruta resultante ya no queda dentro del
 * directorio base, se rechaza: esto evita un ataque de path traversal
 * (CWE-22) donde un valor como "../../../../etc/passwd" permitiría
 * leer archivos fuera del directorio de adjuntos.
 */
@Service
public class AdjuntoServicio {

    static final String DIRECTORIO_ADJUNTOS = "adjuntos-tareas";

    public byte[] leerAdjunto(String nombreArchivo) throws IOException {
        Path directorioBase = Path.of(DIRECTORIO_ADJUNTOS).toAbsolutePath().normalize();
        Path ruta = directorioBase.resolve(nombreArchivo).normalize();

        if (!ruta.startsWith(directorioBase)) {
            throw new AdjuntoInvalidoException(nombreArchivo);
        }

        return Files.readAllBytes(ruta);
    }
}
