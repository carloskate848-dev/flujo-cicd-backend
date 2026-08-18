package com.flujocicd.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Datos de entrada para crear o actualizar una tarea.
 */
public record TareaRequest(

        @NotBlank(message = "El título es obligatorio")
        @Size(max = 150, message = "El título no puede superar los 150 caracteres")
        String titulo,

        @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
        String descripcion
) {
}
