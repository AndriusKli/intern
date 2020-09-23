package uk.co.zenitech.intern.client.musicparams;

public enum Entity {
    MUSIC_ARTIST("musicArtist"),
    MUSIC_TRACK("musicTrack"),
    ALBUM("album");

    private final String value;

    Entity(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
