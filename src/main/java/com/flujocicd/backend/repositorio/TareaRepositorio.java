package com.flujocicd.backend.repositorio;

import com.flujocicd.backend.modelo.Tarea;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TareaRepositorio extends JpaRepository<Tarea, Long> {

    List<Tarea> findByCompletada(boolean completada);
}
