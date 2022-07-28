package de.blutmondgilde.changeloggenerator;

import de.blutmondgilde.changeloggenerator.exception.MissingArgumentException;
import de.blutmondgilde.changeloggenerator.model.ModpackChanges;
import de.blutmondgilde.changeloggenerator.model.ModpackFile;
import de.blutmondgilde.changeloggenerator.utils.ArgumentUtils;
import de.blutmondgilde.changeloggenerator.utils.FileHelper;
import de.blutmondgilde.changeloggenerator.utils.GenerationMode;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Main {
    private static final Path CURRENT_WORKING_DIR = new File(".").toPath();
    public static int REQUEST_DELAY = 50;
    public static GenerationMode GENERATION_MODE = GenerationMode.Markdown;

    public static void main(String[] args) throws IOException {
        String curseForgeToken = ArgumentUtils.getArgument(args, "--token").orElseThrow(() -> new MissingArgumentException("--token"));
        String oldFileName = ArgumentUtils.getArgument(args, "--old").orElseThrow(() -> new MissingArgumentException("--old"));
        String newFileName = ArgumentUtils.getArgument(args, "--new").orElseThrow(() -> new MissingArgumentException("--new"));
        try {
            int requestCount = Integer.parseInt(ArgumentUtils.getArgument(args, "--requests-per-sec").orElse("20"));
            REQUEST_DELAY = 1000 / requestCount;
        } catch (NumberFormatException e) {
            REQUEST_DELAY = 50;
            System.out.println("Invalid '--requests-per-sec' value");
        }

        if (Arrays.stream(args).anyMatch(s -> s.equalsIgnoreCase("--md-minimal"))) {
            GENERATION_MODE = GenerationMode.MarkdownMinimal;
        }

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