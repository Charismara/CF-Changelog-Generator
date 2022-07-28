package de.blutmondgilde.changeloggenerator.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CFModFilesResponse {
    private CFMod[] data;
}
