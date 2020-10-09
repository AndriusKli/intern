package uk.co.zenitech.intern.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Playlist {

    public Playlist(String playlistName) {
        this.playlistName = playlistName;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long playlistId;

    private String playlistName;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.DETACH})
//    @JsonIgnore
    @JoinTable(
            name = "playlistSongs",
            joinColumns = @JoinColumn(name = "playlistId"),
            inverseJoinColumns = @JoinColumn(name = "songId")
    )
    private List<Song> songs;
}
