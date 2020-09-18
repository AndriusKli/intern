package uk.co.zenitech.intern.response;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

public class ITunesResponse {

    private final Long resultCount;
    private final List<JsonNode> results;


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

    @Override
    public String toString() {
        return "ITunesResponse{" +
                "resultCount=" + resultCount +
                ", results=" + results +
                '}';
    }
}
