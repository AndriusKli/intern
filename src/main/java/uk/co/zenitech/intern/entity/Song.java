package uk.co.zenitech.intern.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//import javax.persistence.Entity;

//@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Song {

    @JsonProperty(value = "trackId")
    private Long songId;
    @JsonProperty(value = "trackName")
    private String songName;
    @JsonProperty(value = "collectionName")
    private String albumName;
    private String artistName;
}
