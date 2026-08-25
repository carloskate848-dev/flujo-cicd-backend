package com.flujocicd.backend.controlador;

import com.flujocicd.backend.dto.TareaRequest;
import com.flujocicd.backend.dto.TareaRespuesta;
import com.flujocicd.backend.servicio.AdjuntoServicio;
import com.flujocicd.backend.servicio.TareaServicio;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/tareas")
public class TareaControlador {

    private final TareaServicio tareaServicio;
    private final AdjuntoServicio adjuntoServicio;

    public TareaControlador(TareaServicio tareaServicio, AdjuntoServicio adjuntoServicio) {
        this.tareaServicio = tareaServicio;
        this.adjuntoServicio = adjuntoServicio;
    }

    @GetMapping
    public List<TareaRespuesta> listar(@RequestParam(required = false) Boolean completada) {
        return tareaServicio.listar(completada);
    }

    @GetMapping("/buscar")
    public List<TareaRespuesta> buscar(@RequestParam String texto) {
        return tareaServicio.buscarPorTitulo(texto);
    }

    @GetMapping("/adjuntos")
    public ResponseEntity<byte[]> obtenerAdjunto(@RequestParam String nombre) throws IOException {
        return ResponseEntity.ok(adjuntoServicio.leerAdjunto(nombre));
    }

    @GetMapping("/{id}")
    public TareaRespuesta obtener(@PathVariable Long id) {
        return tareaServicio.obtenerPorId(id);
    }

    @PostMapping
    public ResponseEntity<TareaRespuesta> crear(@Valid @RequestBody TareaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tareaServicio.crear(request));
    }

    @PutMapping("/{id}")
    public TareaRespuesta actualizar(@PathVariable Long id, @Valid @RequestBody TareaRequest request) {
        return tareaServicio.actualizar(id, request);
    }

    @PatchMapping("/{id}/completar")
    public TareaRespuesta marcarCompletada(@PathVariable Long id, @RequestParam(defaultValue = "true") boolean completada) {
        return tareaServicio.marcarCompletada(id, completada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        tareaServicio.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
