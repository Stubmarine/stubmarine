package io.stubmarine.wallraff;

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

        assertThat(title(), equalTo("Stubmarine"));
        find(".logo").withText("Stubmarine").should().exist();

        // Select an inbox
        find("button").withText("Suggest Random Name").click();
        String suggestedName = driver().findElementByCssSelector("#inbox-name").getAttribute("value");
        find("button").withText("Continue").click();

        assertThat(driver().getCurrentUrl(), equalTo("http://" + hostname + ":" + port + "/inbox/" + suggestedName));
        find(".alert-warning")
                .withText("Inbox is empty.Send an email to one of the endpoints above and it will appear here.")
                .should().exist();

        find(".nav-item a").withText("Endpoints").click();

        find("h3").withText("SendGrid").should().exist();
        find("span").withText("https://api.sendgrid.com").should().exist();
        find("span").withText("https://stubmarine.cfapps.io/eapi/sendgrid").should().exist();
        find("span").withText("POST https://stubmarine.cfapps.io/eapi/sendgrid/v3/mail/send <data>").should().exist();

        find("span.token-value").should().exist();
        String token = driver().findElementByCssSelector("span.token-value").getText();

        find(".nav-item a").withText("Emails").click();

        Response sendEmailResponse = sendEmailUsingSendgrid(token);

        assertEquals(sendEmailResponse.getStatusCode(), ACCEPTED.value());

        find("div").withText("From: Sender <sendgrid@example.com>").should().exist();
        find("div").withText("To: Feature Test <featuretest@example.com>, anotherto@example.com").should().exist();
        find("div").withText("Cc: cece@example.com, Seasea <anotherseasea@example.com>").should().exist();
        find("div").withText("Bcc: bee@example.com, Bee Cece <anotherbee@example.com>").should().exist();
        find("div").withText("Subject: Sending with SendGrid is Fun").should().exist();

        find(".email").click();

        find("dd").withText("sendgrid@example.com").should().exist();
        find("dd").withText("Feature Test <featuretest@example.com>, anotherto@example.com").should().exist();
        find("dd").withText("cece@example.com, Seasea <anotherseasea@example.com>").should().exist();
        find("dd").withText("bee@example.com, Bee Cece <anotherbee@example.com>").should().exist();
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
        personalization.addCc(new Email("cece@example.com"));
        personalization.addCc(new Email("anotherseasea@example.com", "Seasea"));
        personalization.addBcc(new Email("bee@example.com"));
        personalization.addBcc(new Email("anotherbee@example.com", "Bee Cece"));
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
