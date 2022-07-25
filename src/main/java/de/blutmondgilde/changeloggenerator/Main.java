package de.blutmondgilde.changeloggenerator;

import de.blutmondgilde.changeloggenerator.exception.MissingArgumentException;
import de.blutmondgilde.changeloggenerator.model.ModpackFile;
import de.blutmondgilde.changeloggenerator.utils.ArgumentUtils;
import de.blutmondgilde.changeloggenerator.utils.FileHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Main {
    private static final Path CURRENT_WORKING_DIR = new File(".").toPath();

    public static void main(String[] args) throws FileNotFoundException {
        String curseForgeToken = ArgumentUtils.getArgument(args, "--token").orElseThrow(() -> new MissingArgumentException("--token"));
        String oldFileName = ArgumentUtils.getArgument(args, "--old").orElseThrow(() -> new MissingArgumentException("--old"));
        String newFileName = ArgumentUtils.getArgument(args, "--new").orElseThrow(() -> new MissingArgumentException("--new"));

        ZipFile oldZipFile = FileHelper.getZip(CURRENT_WORKING_DIR.resolve(oldFileName).toFile());
        ZipFile newZipFile = FileHelper.getZip(CURRENT_WORKING_DIR.resolve(newFileName).toFile());

        ZipEntry oldManifest = FileHelper.findManifest(oldZipFile);
        ZipEntry newManifest = FileHelper.findManifest(newZipFile);

        ModpackFile oldPack = FileHelper.manifestToModpackFile(oldZipFile, oldManifest);
        ModpackFile newPack = FileHelper.manifestToModpackFile(newZipFile, newManifest);

        System.out.println("Old: " + oldPack);
        System.out.println("New: " + newPack);
    }
}