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
    // Might need to change some things in the service if we don't want to get songs when retrieving an individual album.
    private List<Song> songs;

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
