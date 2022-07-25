package de.blutmondgilde.changeloggenerator;

import de.blutmondgilde.changeloggenerator.exception.MissingArgumentException;
import de.blutmondgilde.changeloggenerator.model.ModpackChanges;
import de.blutmondgilde.changeloggenerator.model.ModpackFile;
import de.blutmondgilde.changeloggenerator.utils.ArgumentUtils;
import de.blutmondgilde.changeloggenerator.utils.FileHelper;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Main {
    private static final Path CURRENT_WORKING_DIR = new File(".").toPath();

    public static void main(String[] args) throws IOException {
        String curseForgeToken = ArgumentUtils.getArgument(args, "--token").orElseThrow(() -> new MissingArgumentException("--token"));
        String oldFileName = ArgumentUtils.getArgument(args, "--old").orElseThrow(() -> new MissingArgumentException("--old"));
        String newFileName = ArgumentUtils.getArgument(args, "--new").orElseThrow(() -> new MissingArgumentException("--new"));

        ZipFile oldZipFile = FileHelper.getZip(CURRENT_WORKING_DIR.resolve(oldFileName).toFile());
        ZipFile newZipFile = FileHelper.getZip(CURRENT_WORKING_DIR.resolve(newFileName).toFile());

        ZipEntry oldManifest = FileHelper.findManifest(oldZipFile);
        ZipEntry newManifest = FileHelper.findManifest(newZipFile);

        ModpackFile oldPack = FileHelper.manifestToModpackFile(oldZipFile, oldManifest);
        ModpackFile newPack = FileHelper.manifestToModpackFile(newZipFile, newManifest);

        System.out.println("Checking Files...");
        ModpackChanges changes = new ModpackChanges(oldPack, newPack, curseForgeToken);
        System.out.println("Requesting Data from CurseForge...");
        String markdownValue = changes.getChanges();
        System.out.println("Generating Markdown Changelog...");
        Files.write(CURRENT_WORKING_DIR.resolve("markdown.md"), markdownValue.getBytes(StandardCharsets.UTF_8));
        System.out.println("Done");
        System.exit(0);
    }
}