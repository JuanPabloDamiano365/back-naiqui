package com.uade.e_commerce.exception;

public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String mensaje) {
        super(mensaje);
    }
}
