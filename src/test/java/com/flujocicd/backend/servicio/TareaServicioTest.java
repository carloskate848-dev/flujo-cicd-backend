package com.flujocicd.backend.servicio;

import com.flujocicd.backend.dto.TareaRequest;
import com.flujocicd.backend.dto.TareaRespuesta;
import com.flujocicd.backend.excepcion.TareaNoEncontradaException;
import com.flujocicd.backend.modelo.Tarea;
import com.flujocicd.backend.repositorio.BusquedaTareaRepositorio;
import com.flujocicd.backend.repositorio.TareaRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TareaServicioTest {

    @Mock
    private TareaRepositorio tareaRepositorio;

    @Mock
    private BusquedaTareaRepositorio busquedaTareaRepositorio;

    private TareaServicio tareaServicio;

    @BeforeEach
    void configurar() {
        tareaServicio = new TareaServicio(tareaRepositorio, busquedaTareaRepositorio);
    }

    @Test
    void listarSinFiltroDevuelveTodasLasTareas() {
        when(tareaRepositorio.findAll()).thenReturn(List.of(
                new Tarea("Tarea 1", "desc 1"),
                new Tarea("Tarea 2", "desc 2")
        ));

        List<TareaRespuesta> resultado = tareaServicio.listar(null);

        assertThat(resultado).hasSize(2);
        verify(tareaRepositorio, never()).findByCompletada(anyBoolean());
    }

    @Test
    void listarConFiltroDelegaEnFindByCompletada() {
        when(tareaRepositorio.findByCompletada(true)).thenReturn(List.of(new Tarea("Hecha", null)));

        List<TareaRespuesta> resultado = tareaServicio.listar(true);

        assertThat(resultado).hasSize(1);
        verify(tareaRepositorio, times(1)).findByCompletada(true);
    }

    @Test
    void obtenerPorIdExistenteDevuelveLaTarea() {
        Tarea tarea = new Tarea("Titulo", "Descripcion");
        when(tareaRepositorio.findById(1L)).thenReturn(Optional.of(tarea));

        TareaRespuesta respuesta = tareaServicio.obtenerPorId(1L);

        assertThat(respuesta.titulo()).isEqualTo("Titulo");
    }

    @Test
    void obtenerPorIdInexistenteLanzaExcepcion() {
        when(tareaRepositorio.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tareaServicio.obtenerPorId(99L))
                .isInstanceOf(TareaNoEncontradaException.class)
                .hasMessageContaining("99");
    }

    @Test
    void crearGuardaUnaNuevaTarea() {
        TareaRequest request = new TareaRequest("Nueva tarea", "detalle");
        when(tareaRepositorio.save(any(Tarea.class))).thenAnswer(inv -> inv.getArgument(0));

        TareaRespuesta respuesta = tareaServicio.crear(request);

        assertThat(respuesta.titulo()).isEqualTo("Nueva tarea");
        assertThat(respuesta.completada()).isFalse();
    }

    @Test
    void marcarCompletadaActualizaElEstado() {
        Tarea tarea = new Tarea("Titulo", "Descripcion");
        when(tareaRepositorio.findById(1L)).thenReturn(Optional.of(tarea));
        when(tareaRepositorio.save(any(Tarea.class))).thenAnswer(inv -> inv.getArgument(0));

        TareaRespuesta respuesta = tareaServicio.marcarCompletada(1L, true);

        assertThat(respuesta.completada()).isTrue();
    }

    @Test
    void eliminarTareaInexistenteLanzaExcepcion() {
        when(tareaRepositorio.existsById(5L)).thenReturn(false);

        assertThatThrownBy(() -> tareaServicio.eliminar(5L))
                .isInstanceOf(TareaNoEncontradaException.class);

        verify(tareaRepositorio, never()).deleteById(anyLong());
    }

    @Test
    void eliminarTareaExistenteLaBorra() {
        when(tareaRepositorio.existsById(1L)).thenReturn(true);

        tareaServicio.eliminar(1L);

        verify(tareaRepositorio, times(1)).deleteById(1L);
    }
}
