package com.github.aleksandy.petrovich;

import java.io.Serial;

/**
 * Common exception thrown when it's impossible to inflect a name due to any reasons.
 */
public class PetrovichException extends RuntimeException {

    @Serial private static final long serialVersionUID = -7970903626881485828L;

    public PetrovichException(String message) {
        super(message);
    }

}
