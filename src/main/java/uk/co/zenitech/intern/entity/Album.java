package uk.co.zenitech.intern.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Album {

    @Id
    @JsonProperty(value = "collectionId")
    private Long albumId;

    private Long artistId;

    @JsonProperty(value = "collectionName")
    private String albumName;

    @JsonProperty(value = "artworkUrl100")
    private String albumCover;

    private Long trackCount;

    @JsonProperty(value = "primaryGenreName")
    private String genre;

    @OneToMany(mappedBy = "album", cascade = CascadeType.ALL)
//    @JsonIgnore
    private List<Song> songs;
// TODO check if assigning an empty array removes the need for nullcheck @ service
    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }

    @Override
    public String toString() {
        return "Album{" +
                "albumId=" + albumId +
                ", artistId=" + artistId +
                ", albumName='" + albumName + '\'' +
                ", albumCover='" + albumCover + '\'' +
                ", trackCount=" + trackCount +
                ", genre='" + genre + '\'' +
                ", songs=" + songs +
                '}';
    }
}
