package uk.co.zenitech.intern.errorhandler;

public class ErrorResponse {
    private final String url;
    private final String error;

    public ErrorResponse(String url, String error) {
        this.url = url;
        this.error = error;
    }

    public String getUrl() {
        return url;
    }

    public String getError() {
        return error;
    }
}
