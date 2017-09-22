package io.stubmarine.wallraff.api;

import io.stubmarine.wallraff.sendgrid.SendGridTokenGenerator;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static java.util.Arrays.asList;

@RestController
public class EndpointController {

    private final SendGridTokenGenerator sendGridTokenGenerator;

    public EndpointController(
            SendGridTokenGenerator sendGridTokenGenerator
    ) {
        this.sendGridTokenGenerator = sendGridTokenGenerator;
    }

    @RequestMapping("/api/inbox/{inboxName}/endpoints")
    public List list(
            @PathVariable("inboxName") String inbox
    ) {
        return asList(
                new EndpointListItem(
                        "sendgrid",
                        "SendGrid",
                        "https://api.sendgrid.com",
                        "https://stubmarine.cfapps.io/eapi/sendgrid",
                        sendGridTokenGenerator.generateToken(inbox),
                        "POST https://stubmarine.cfapps.io/eapi/sendgrid/v3/mail/send <data>"
                )
        );
    }

    public class EndpointListItem {
        private final String id;
        private final String name;
        private final String originalHost;
        private final String newHost;
        private final String newToken;
        private final String example;

        public EndpointListItem(
                String id,
                String name,
                String originalHost,
                String newHost,
                String newToken,
                String example
        ) {
            this.id = id;
            this.name = name;
            this.originalHost = originalHost;
            this.newHost = newHost;
            this.newToken = newToken;
            this.example = example;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getOriginalHost() {
            return originalHost;
        }

        public String getNewHost() {
            return newHost;
        }

        public String getNewToken() {
            return newToken;
        }

        public String getExample() {
            return example;
        }
    }
}
