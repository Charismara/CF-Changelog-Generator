package de.blutmondgilde.changeloggenerator.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CFModLoader {
    private int id, gameVersionId, minecraftGameVersionId, type, modLoaderGameVersionTypeId, mcGameVersionId, mcGameVersionTypeId;
    private String forgeVersion,name;
}
