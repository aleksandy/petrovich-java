package com.github.aleksandy.petrovich.exception;

public abstract class PetrovichException extends Exception {

    private static final long serialVersionUID = 1L;

    public PetrovichException(String message) {
        super(message);
    }

}
