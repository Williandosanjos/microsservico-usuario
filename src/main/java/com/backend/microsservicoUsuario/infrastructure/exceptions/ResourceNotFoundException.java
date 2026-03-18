package com.backend.microsservicoUsuario.infrastructure.exceptions;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String mensage){
        super(mensage);
    }

    public ResourceNotFoundException(String mensage, Throwable throwable){
        super(mensage, throwable);
    }
}
