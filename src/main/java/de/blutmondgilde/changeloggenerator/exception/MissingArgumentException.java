package de.blutmondgilde.changeloggenerator.exception;

public class MissingArgumentException extends IllegalArgumentException {
    public MissingArgumentException(String argumentName) {
        super(String.format("Missing Argument: %s", argumentName));
    }
}
