package com.flujocicd.backend.servicio;

import com.flujocicd.backend.dto.TareaRequest;
import com.flujocicd.backend.dto.TareaRespuesta;
import com.flujocicd.backend.excepcion.TareaNoEncontradaException;
import com.flujocicd.backend.modelo.Tarea;
import com.flujocicd.backend.repositorio.BusquedaTareaRepositorio;
import com.flujocicd.backend.repositorio.TareaRepositorio;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TareaServicio {

    private final TareaRepositorio tareaRepositorio;
    private final BusquedaTareaRepositorio busquedaTareaRepositorio;

    public TareaServicio(TareaRepositorio tareaRepositorio, BusquedaTareaRepositorio busquedaTareaRepositorio) {
        this.tareaRepositorio = tareaRepositorio;
        this.busquedaTareaRepositorio = busquedaTareaRepositorio;
    }

    public List<TareaRespuesta> listar(Boolean completada) {
        List<Tarea> tareas = completada == null
                ? tareaRepositorio.findAll()
                : tareaRepositorio.findByCompletada(completada);

        return tareas.stream().map(TareaRespuesta::desde).toList();
    }

    public TareaRespuesta obtenerPorId(Long id) {
        return TareaRespuesta.desde(buscarOFallar(id));
    }

    public TareaRespuesta crear(TareaRequest request) {
        Tarea tarea = new Tarea(request.titulo(), request.descripcion());
        return TareaRespuesta.desde(tareaRepositorio.save(tarea));
    }

    public TareaRespuesta actualizar(Long id, TareaRequest request) {
        Tarea tarea = buscarOFallar(id);
        tarea.setTitulo(request.titulo());
        tarea.setDescripcion(request.descripcion());
        return TareaRespuesta.desde(tareaRepositorio.save(tarea));
    }

    public TareaRespuesta marcarCompletada(Long id, boolean completada) {
        Tarea tarea = buscarOFallar(id);
        tarea.setCompletada(completada);
        return TareaRespuesta.desde(tareaRepositorio.save(tarea));
    }

    public void eliminar(Long id) {
        if (!tareaRepositorio.existsById(id)) {
            throw new TareaNoEncontradaException(id);
        }
        tareaRepositorio.deleteById(id);
    }

    public List<TareaRespuesta> buscarPorTitulo(String texto) {
        return busquedaTareaRepositorio.buscarPorTitulo(texto).stream()
                .map(TareaRespuesta::desde)
                .toList();
    }

    private Tarea buscarOFallar(Long id) {
        return tareaRepositorio.findById(id)
                .orElseThrow(() -> new TareaNoEncontradaException(id));
    }
}
