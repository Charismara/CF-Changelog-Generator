package de.blutmondgilde.changeloggenerator.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModpackFile {
    private de.blutmondgilde.changeloggenerator.model.Minecraft minecraft;
    private String manifestType, overrides, version, author, name;
    private int manifestVersion;
    private de.blutmondgilde.changeloggenerator.model.ModFile[] files;
}
