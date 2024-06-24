package com.example.ventaComputadora.domain.Exception;


public class ProductoYaEnFavoritosException extends RuntimeException {
    public ProductoYaEnFavoritosException(String message) {
        super(message);
    }
}
