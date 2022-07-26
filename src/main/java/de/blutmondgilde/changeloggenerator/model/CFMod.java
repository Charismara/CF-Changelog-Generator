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
public class CFMod {
    private int id, gameId, downloadCount;
    private String name, slug;
    private CFModLinks links;
    @JsonAlias({"dateReleased", "fileDate"})
    private DateTime creationDate;
}
