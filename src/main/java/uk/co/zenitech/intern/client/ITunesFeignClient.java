package uk.co.zenitech.intern.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uk.co.zenitech.intern.response.ITunesResponse;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@FeignClient(name = "feignClient", url = "${itunes.endpoint}", configuration = FeignConfig.class)
@Validated
public interface ITunesFeignClient {

    @GetMapping(value = "search?term={searchTerm}&entity={entity}&attribute={attribute}&limit=200", produces = MediaType.APPLICATION_JSON_VALUE)
    ITunesResponse getResults(@RequestParam String searchTerm,
                                      @RequestParam String entity,
                                      @RequestParam String attribute,
                                      @RequestParam(required = false) @Min(1) @Max(200) Long limit
    );

    @GetMapping(value = "lookup?id={id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ITunesResponse getById(@RequestParam Long id);

    @GetMapping(value = "lookup?id={id}&entity=song", produces = MediaType.APPLICATION_JSON_VALUE)
    ITunesResponse getAlbumSongs(@RequestParam Long id);
}
