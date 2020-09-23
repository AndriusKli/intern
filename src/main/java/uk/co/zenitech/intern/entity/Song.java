package uk.co.zenitech.intern.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Song {

    @Id
    @JsonProperty(value = "trackId")
    private Long songId;
    @JsonProperty(value = "trackName")
    @NotNull
    private String songName;
    @JsonProperty(value = "collectionName")
    private String albumName;
    private String artistName;
}
