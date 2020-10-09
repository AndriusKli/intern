package uk.co.zenitech.intern.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Song {

    public Song(Long songId, @NotNull String songName, String albumName, String artistName) {
        this.songId = songId;
        this.songName = songName;
        this.albumName = albumName;
        this.artistName = artistName;
    }

    @Id
    @JsonProperty(value = "trackId")
    private Long songId;

    @JsonProperty(value = "trackName")
    @NotNull
    private String songName;

    @JsonProperty(value = "collectionName")
    private String albumName;

    private String artistName;

    @JsonProperty(value = "artworkUrl100")
    private String albumCover;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "albumId")
    @JsonIgnore
    private Album album;

    @ManyToMany(mappedBy = "songs")
    @JsonIgnore
    private List<Playlist> playlists;

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    @Override
    public String toString() {
        return "Song{" +
                "songId=" + songId +
                ", songName='" + songName + '\'' +
                ", albumName='" + albumName + '\'' +
                ", artistName='" + artistName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Song song = (Song) o;
        return Objects.equals(songId, song.songId) &&
                Objects.equals(songName, song.songName) &&
                Objects.equals(albumName, song.albumName) &&
                Objects.equals(artistName, song.artistName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(songId, songName, albumName, artistName);
    }
}
