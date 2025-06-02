package udemy.clone.exceptions;

import java.util.NoSuchElementException;

public class EntityNotFoundException extends NoSuchElementException {
    public EntityNotFoundException(String message) {
        super(message);
    }
}
