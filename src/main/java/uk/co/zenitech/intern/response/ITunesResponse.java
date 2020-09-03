package uk.co.zenitech.intern.response;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

public class ITunesResponse {

    private Long resultCount;
    private List<JsonNode> results;

    public ITunesResponse() {
    }

    public ITunesResponse(Long resultCount, List<JsonNode> results) {
        this.resultCount = resultCount;
        this.results = results;
    }

    public Long getResultCount() {
        return resultCount;
    }

    public List<JsonNode> getResults() {
        return results;
    }
}
