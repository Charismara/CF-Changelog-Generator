package de.blutmondgilde.changeloggenerator.exception;

import java.io.File;

public class InvalidZipException extends RuntimeException {
    public InvalidZipException(File file) {
        super(String.format("File %s is not a .zip file", file.getName()));
    }
}
