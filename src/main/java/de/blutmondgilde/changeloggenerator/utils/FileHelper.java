package de.blutmondgilde.changeloggenerator.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.blutmondgilde.changeloggenerator.exception.InvalidZipException;
import de.blutmondgilde.changeloggenerator.model.ModpackFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class FileHelper {
    public static ZipFile getZip(File file) {
        try {
            if (!file.exists()) throw new FileNotFoundException(file.getAbsolutePath());
            return new ZipFile(file);
        } catch (ZipException e) {
            throw new InvalidZipException(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static ZipEntry findManifest(ZipFile file) throws FileNotFoundException {
        Enumeration<? extends ZipEntry> entries = file.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            if (entry.getName().equals("manifest.json")) {
                return entry;
            }
        }

        throw new FileNotFoundException("Could not find manifest.json in ZipFile: " + file.getName());
    }

    public static ModpackFile manifestToModpackFile(ZipFile file, ZipEntry manifestEntry) {
        try {
            InputStream stream = file.getInputStream(manifestEntry);
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(stream, ModpackFile.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
