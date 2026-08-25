package com.flujocicd.backend.excepcion;

public class AdjuntoInvalidoException extends RuntimeException {

    public AdjuntoInvalidoException(String nombreArchivo) {
        super("Nombre de adjunto inválido: " + nombreArchivo);
    }
}
