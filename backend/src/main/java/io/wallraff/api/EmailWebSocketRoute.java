package io.wallraff.api;

import org.springframework.stereotype.Service;

import java.net.URI;

@Service
public class EmailWebSocketRoute {
    public String getEndpointMask() {
        return "/wsapi/inbox/*/emails";
    }

    public String extractInboxName(URI uri) {
        return uri.getPath().split("/")[3];
    }
}
