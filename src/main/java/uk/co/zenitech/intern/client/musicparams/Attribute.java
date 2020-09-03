package uk.co.zenitech.intern.client.musicparams;

public enum Attribute {
    SONG_TERM("songTerm"),
    ARTIST_TERM("artistTerm");

    private final String value;

    Attribute(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
