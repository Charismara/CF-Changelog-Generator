package de.blutmondgilde.changeloggenerator.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CFMod {
    private int id, gameId, downloadCount;
    private String name, slug;
    private CFModLinks links;
}
