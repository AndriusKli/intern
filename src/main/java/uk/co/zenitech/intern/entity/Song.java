package uk.co.zenitech.intern.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//import javax.persistence.Entity;

//@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Song {

    private Long songId;
    private String songName;
    private String albumName;
    private String artistName;
}
