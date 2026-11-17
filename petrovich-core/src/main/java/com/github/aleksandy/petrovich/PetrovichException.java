package com.github.aleksandy.petrovich;

import java.io.Serial;

/**
 * Общее исключение, выбрасываемое, когда имя невозможно просклонять по какой-либо причине.
 */
public class PetrovichException extends RuntimeException {

    @Serial private static final long serialVersionUID = -7970903626881485828L;

    /**
     * Создаёт исключение с указанным сообщением.
     *
     * @param message описание причины
     */
    public PetrovichException(String message) {
        super(message);
    }

}
