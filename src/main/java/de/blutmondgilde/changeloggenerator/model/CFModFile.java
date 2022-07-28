package de.blutmondgilde.changeloggenerator.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CFModFile {
    private int id, gameId, modId, downloadCount;
    private String displayName;
    private CFModLinks links;
    @JsonAlias({"fileDate"})
    private DateTime creationDate;
}
