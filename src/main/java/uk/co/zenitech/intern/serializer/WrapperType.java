package uk.co.zenitech.intern.serializer;

public enum WrapperType {
    TRACK("track"),
    ARTIST("artist"),
    COLLECTION("collection");

    private final String wrapper;

    WrapperType(String wrapper) {
        this.wrapper = wrapper;
    }

    public String getWrapper() {
        return wrapper;
    }
}
