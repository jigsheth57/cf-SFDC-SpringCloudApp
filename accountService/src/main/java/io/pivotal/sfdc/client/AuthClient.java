package io.pivotal.sfdc.client;

import com.force.api.ApiSession;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "authservice")
public interface AuthClient {

    @GetMapping("/oauth2")
    ApiSession getApiSession();

    @GetMapping("/invalidateSession")
    ApiSession invalidateSession();
}
