package com.flujocicd.backend.servicio;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Servicio para leer los adjuntos que el usuario sube a una tarea.
 *
 * ATENCIÓN - FALLA INTENCIONAL PARA EL EJERCICIO DE CI/CD:
 * El método leerAdjunto concatena el nombre de archivo recibido del
 * usuario directamente en la ruta del sistema de archivos, sin
 * normalizar ni validar el resultado contra el directorio base. Esto
 * habilita un ataque de path traversal (CWE-22): un valor como
 * "../../../../etc/passwd" permite leer archivos fuera del directorio
 * de adjuntos. Es el tipo de hallazgo que CodeQL marca con la regla
 * java/path-injection.
 *
 * La corrección (a modo de ejercicio) es normalizar la ruta resultante
 * y verificar con Path#startsWith() que siga estando dentro del
 * directorio base antes de leerla.
 */
@Service
public class AdjuntoServicio {

    static final String DIRECTORIO_ADJUNTOS = "adjuntos-tareas";

    public byte[] leerAdjunto(String nombreArchivo) throws IOException {
        // Vulnerable a propósito (fines de ejercicio): no usar en producción.
        File archivo = new File(DIRECTORIO_ADJUNTOS + File.separator + nombreArchivo);
        try (FileInputStream entrada = new FileInputStream(archivo)) {
            return entrada.readAllBytes();
        }
    }
}
