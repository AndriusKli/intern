package uk.co.zenitech.intern.errorhandling.exceptions;

public class EntityNotInDbException extends RuntimeException {
    public EntityNotInDbException(String entity, Long id) {
        super("The requested " + entity + " with the id " + id.toString() + " was not found in the database.");
    }

    public EntityNotInDbException(String entity, String id) {
        super("The requested " + entity + " with the id " + id + " was not found in the database.");
    }

    public EntityNotInDbException(String entity) {
        super("No " + entity + " found in the database.");
    }
}
