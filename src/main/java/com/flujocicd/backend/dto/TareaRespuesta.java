package com.flujocicd.backend.dto;

import com.flujocicd.backend.modelo.Tarea;

import java.time.LocalDateTime;

/**
 * Representación de una tarea expuesta por la API.
 */
public record TareaRespuesta(
        Long id,
        String titulo,
        String descripcion,
        boolean completada,
        LocalDateTime fechaCreacion
) {

    public static TareaRespuesta desde(Tarea tarea) {
        return new TareaRespuesta(
                tarea.getId(),
                tarea.getTitulo(),
                tarea.getDescripcion(),
                tarea.isCompletada(),
                tarea.getFechaCreacion()
        );
    }
}
