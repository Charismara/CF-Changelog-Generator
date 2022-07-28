package de.blutmondgilde.changeloggenerator.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModpackFile {
    private Minecraft minecraft;
    private String manifestType, overrides, version, author, name;
    private int manifestVersion;
    private ModFile[] files;
}
