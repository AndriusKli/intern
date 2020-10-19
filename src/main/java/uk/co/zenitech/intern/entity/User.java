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
@Table(name = "itunes_user")
public class User {

    public User(Long uid, String userName) {
        this.uid = uid;
        this.userName = userName;
    }

    public User(Long uid, String userName, String email) {
        this.uid = uid;
        this.userName = userName;
        this.email = email;
    }

    @Id
    @Column(name = "user_id")
    private Long uid;

    private String userName;

    private String email;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Playlist> playlists;

    @Override
    public String toString() {
        return "User{" +
                "userId=" + uid +
                ", userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
