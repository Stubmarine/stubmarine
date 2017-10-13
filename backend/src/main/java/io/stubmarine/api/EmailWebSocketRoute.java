package io.stubmarine.api;

import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

import java.net.URI;

@Service
public class EmailWebSocketRoute {

    private static final String PATH = "/wsapi/inbox/{inboxName}/emails";

    private final AntPathMatcher antPathMatcher;

    public EmailWebSocketRoute() {
        antPathMatcher = new AntPathMatcher();
    }

    public String getPath() {
        return PATH;
    }

    public String extractInboxName(URI uri) {
        return antPathMatcher
                .extractUriTemplateVariables(PATH, uri.getPath())
                .get("inboxName");
    }
}
