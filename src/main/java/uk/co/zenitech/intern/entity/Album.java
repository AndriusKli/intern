package uk.co.zenitech.intern.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Album {

    @Id
    @JsonProperty(value = "collectionId")
    private Long albumId;

    private Long artistId;

    @JsonProperty(value = "artworkUrl100")
    private String albumCover;

    private Long trackCount;

    @JsonProperty(value = "primaryGenreName")
    private String genre;
}
