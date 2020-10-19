package uk.co.zenitech.intern.client;

import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "authClient", url = "https://dev-bczsc8sa.us.auth0.com/", configuration = FeignConfig.class)
public interface AuthClient {

    @GetMapping(value = "userinfo/?access_token={token}", produces = MediaType.APPLICATION_JSON_VALUE)
    Response getUserInfo(@RequestParam String token);

    @GetMapping(value = "pem", produces = MediaType.APPLICATION_JSON_VALUE)
    Response getPublicKey();
}
