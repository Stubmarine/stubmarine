package io.wallraff;

import com.sendgrid.*;
import net.codestory.simplelenium.SeleniumTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.ACCEPTED;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = WallraffApplication.class,
        webEnvironment = RANDOM_PORT,
        properties = {"jwtSecret=secret"}
)
public class HappyPathTest extends SeleniumTest {
    @LocalServerPort
    private int port;

    private final String hostname = "localhost";

    @Before
    public void beforeEach() {
        System.setProperty("browser", "chrome");
    }

    @Override
    protected String getDefaultBaseUrl() {
        return "http://" + hostname + ":" + port + "/";
    }

    @Test()
    public void testSendingAnEmailAppears() throws Exception {
        goTo(getDefaultBaseUrl());

        assertThat(title(), equalTo("Wallraff"));
        find(".logo").withText("Wallraff").should().exist();

        // Select an inbox
        find("button").withText("Suggest Random Name").click();
        find("button").withText("Continue").click();

        find(".alert-warning")
                .withText("Inbox is empty.Send an email to one of the endpoints above and it will appear here.")
                .should().exist();

        find(".nav-item a").withText("Endpoints").click();

        find("h3").withText("SendGrid").should().exist();
        find("span").withText("https://api.sendgrid.com").should().exist();
        find("span").withText("https://wallraff.cfapps.io/eapi/sendgrid").should().exist();
        find("span").withText("POST https://wallraff.cfapps.io/eapi/sendgrid/v3/mail/send <data>").should().exist();

        find("span.token-value").should().exist();
        String token = driver().findElementByCssSelector("span.token-value").getText();

        find(".nav-item a").withText("Emails").click();

        Response sendEmailResponse = sendEmailUsingSendgrid(token);

        assertEquals(sendEmailResponse.getStatusCode(), ACCEPTED.value());

        find("div").withText("From: Sender <sendgrid@example.com>").should().exist();
        find("div").withText("To: Feature Test <featuretest@example.com>, anotherto@example.com").should().exist();
        find("div").withText("Subject: Sending with SendGrid is Fun").should().exist();

        find(".email").click();

        find("dd").withText("sendgrid@example.com").should().exist();
        find("dd").withText("Feature Test <featuretest@example.com>, anotherto@example.com").should().exist();
        find("dd").withText("Sending with SendGrid is Fun").should().exist();
        find(".email-detail--body").withText("B0dy");
    }

    @Test
    public void testSendingWithIncorrectToken() throws Exception {
        try {
            sendEmailUsingSendgrid("INCORRECT_TOKEN");
        } catch (IOException ioException) {
            assertEquals("Request returned status Code 401Body:", ioException.getMessage());
        } catch (Exception ex) {
            throw ex;
        }
    }

    private Response sendEmailUsingSendgrid(String token) throws Exception {
        Email from = new Email("sendgrid@example.com", "Sender");
        String subject = "Sending with SendGrid is Fun";
        Email to = new Email("featuretest@example.com", "Feature Test");
        Content content = new Content("text/plain", "B0dy");
        Mail mail = new Mail(from, subject, to, content);

        Personalization personalization = new Personalization();
        personalization.addTo(new Email("anotherto@example.com"));
        mail.addPersonalization(personalization);

        SendGrid sg = new SendGrid(token, true);
        sg.setHost(hostname + ":" + port + "/eapi/sendgrid");

        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());

        return sg.api(request);
    }
}
