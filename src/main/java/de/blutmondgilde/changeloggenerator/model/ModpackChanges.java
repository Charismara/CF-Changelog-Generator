package de.blutmondgilde.changeloggenerator.model;

import de.blutmondgilde.changeloggenerator.Main;
import de.blutmondgilde.changeloggenerator.utils.CurseForgeAPI;
import de.blutmondgilde.changeloggenerator.utils.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ModpackChanges {

    private final List<ModFile> newMods = new ArrayList<>();

    private final List<Pair<ModFile, ModFile>> changedMods = new ArrayList<>();

    private final List<ModFile> removedMods = new ArrayList<>();
    private final CurseForgeAPI api;
    private String modLoaderUpdateString = "";
    private final CFModLoader modLoader;

    public ModpackChanges(ModpackFile oldPack, ModpackFile newPack, String token) {
        this.api = new CurseForgeAPI(token);
        List<ModFile> oldMods = new ArrayList<>(Arrays.asList(oldPack.getFiles()));
        List<ModFile> newMods = Arrays.asList(newPack.getFiles());

        System.out.println("Loading Modloader information...");
        this.modLoader = api.getModLoader(newPack.getMinecraft());
        if (!newPack.getMinecraft().getModLoaders()[0].getId().equals(oldPack.getMinecraft().getModLoaders()[0].getId())) {
            String oldForgeVersion = api.getModLoader(oldPack.getMinecraft()).getForgeVersion();
            String newForgeVersion = modLoader.getForgeVersion();
            modLoaderUpdateString = String.format("%s → %s", oldForgeVersion, newForgeVersion);
        }


        newMods.forEach(mod -> {
            Optional<ModFile> oldModData = oldMods.stream()
                .filter(modFile -> modFile.getProjectID() == mod.getProjectID())
                .findFirst();
            if (oldModData.isPresent()) {
                if (oldModData.get().getFileID() != mod.getFileID()) {
                    this.changedMods.add(Pair.of(oldModData.get(), mod)); //Add changed
                }
                oldMods.remove(oldModData.get());
            } else {
                this.newMods.add(mod); //Add new
            }
        });

        removedMods.addAll(oldMods); //Add removed
    }

    public String getChanges() {
        StringBuilder builder = new StringBuilder();
        switch (Main.GENERATION_MODE) {
            default:
            case Markdown: {
                if (!modLoaderUpdateString.isEmpty()) {
                    builder.append(String.format("# %s Update:\n", this.modLoader.getName()))
                        .append(modLoaderUpdateString)
                        .append("\n\n");
                }

                builder.append("# **New Mods**\n");
                if (newMods.isEmpty()) {
                    builder.append("## None\n\n");
                } else {
                    newMods.forEach(modFile -> {
                        CFMod mod = this.api.getModInformation(modFile);
                        builder.append("## ").append(mod.getName())
                            .append("\n[CurseForge Link](https://www.curseforge.com/minecraft/mc-mods/")
                            .append(mod.getSlug())
                            .append(")\n\n");
                    });
                }
                System.out.printf("Found %s new Mods%n", newMods.size());

                builder.append("\n_________________\n# **Changed Mods**\n");
                if (changedMods.isEmpty()) {
                    builder.append("## None\n\n");
                } else {
                    changedMods.forEach(file -> {
                        CFModFile mod = this.api.getFile(file.getRight());
                        CFMod project = this.api.getModInformation(file.getRight());
                        builder.append("## ").append(project.getName())
                            .append("\n[CurseForge Link](https://www.curseforge.com/minecraft/mc-mods/")
                            .append(project.getSlug())
                            .append(")\n\n");
                        builder.append("### ").append(mod.getDisplayName()).append("\n");
                        builder.append(this.api.getModChangeLog(file.getRight())).append("\n");
                        System.out.printf("Checking for skipped files for %s%n", project.getName());
                        List<CFModFile> skippedUpdates = api.getFilesBetween(this.modLoader, file.getLeft(), file.getRight());
                        if (skippedUpdates.size() > 0) {
                            System.out.printf("Loading %s skipped changelogs for %s%n", skippedUpdates.size(), project.getName());
                            skippedUpdates.forEach(cfMod -> {
                                builder.append("\n### ").append(cfMod.getDisplayName()).append("\n");
                                builder.append(this.api.getModChangeLog(new ModFile(cfMod.getModId(), cfMod.getId(), false))).append("\n");
                            });
                        }
                        builder.append("\n");
                    });
                }
                System.out.printf("Found %s changed Mods%n", changedMods.size());

                builder.append("\n_________________\n# **Removed Mods**\n\n");
                if (removedMods.isEmpty()) {
                    builder.append("## None\n\n");
                } else {
                    removedMods.forEach(modFile -> {
                        CFMod mod = this.api.getModInformation(modFile);
                        builder.append("## ").append(mod.getName())
                            .append("\n[CurseForge Link](https://www.curseforge.com/minecraft/mc-mods/")
                            .append(mod.getSlug())
                            .append(")\n\n");
                    });
                }
                System.out.printf("Found %s removed Mods%n", removedMods.size());

                builder.append("\n_________________\n\n").append("Changelog generated by [CF-Changelog-Generator](https://github.com/Charismara/CF-Changelog-Generator)");
            }
            break;

            case MarkdownMinimal: {
                if (!newMods.isEmpty()) {
                    builder.append("# **New Mods**\n");
                    newMods.forEach(modFile -> {
                        CFMod mod = this.api.getModInformation(modFile);
                        applyMinimalModData(builder, mod);
                    });
                }
                System.out.printf("Found %s new Mods%n", newMods.size());

                if (!changedMods.isEmpty()) {
                    builder.append("\n_________________\n# **Changed Mods**\n");
                    changedMods.forEach(modFile -> {
                        CFMod mod = this.api.getModInformation(modFile.getRight());
                        applyMinimalModData(builder, mod);
                    });
                }
                System.out.printf("Found %s changed Mods%n", changedMods.size());

                if (!removedMods.isEmpty()) {
                    builder.append("\n_________________\n# **Removed Mods**\n\n");
                    removedMods.forEach(modFile -> {
                        CFMod mod = this.api.getModInformation(modFile);
                        applyMinimalModData(builder, mod);
                    });
                }
                System.out.printf("Found %s removed Mods%n", removedMods.size());

                builder.append("\n_________________\n\n").append("Changelog generated by [CF-Changelog-Generator](https://github.com/Charismara/CF-Changelog-Generator)");
            }
        }

        return builder.toString();
    }


    private void applyMinimalModData(StringBuilder builder, CFMod mod) {
        builder.append("- ").append(mod.getName())
            .append(" - [CurseForge Link](https://www.curseforge.com/minecraft/mc-mods/")
            .append(mod.getSlug())
            .append(")\n\n");
    }
}
