package de.blutmondgilde.changeloggenerator.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModFile {
    private int projectID, fileID;
    private boolean required;
}
