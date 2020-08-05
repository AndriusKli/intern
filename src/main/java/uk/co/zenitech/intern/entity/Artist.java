package uk.co.zenitech.intern.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//import javax.persistence.Entity;

//@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Artist {

    private Long amgArtistId;
    private Long artistId;
    private String artistName;
}
