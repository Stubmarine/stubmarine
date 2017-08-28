package io.wallraff;

import com.sendgrid.*;
import org.fluentlenium.adapter.junit.FluentTest;
import org.fluentlenium.core.domain.FluentList;
import org.fluentlenium.core.domain.FluentWebElement;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static org.fluentlenium.core.filter.FilterConstructor.withText;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = WallraffApplication.class,
        webEnvironment = RANDOM_PORT,
        properties = {"jwtSecret=secret"}
)
public class HappyPathTest extends FluentTest {

    @LocalServerPort
    private int port;

    private final String hostname = "localhost";

    @Override
    public WebDriver newWebDriver() {
        ChromeOptions chromeOptions = new ChromeOptions();
        return new ChromeDriver(chromeOptions);
    }

    @Test()
    public void testSendingAnEmailAppears() throws Exception {
        goTo("http://" + hostname + ":" + port);

        assertThat(window().title(), equalTo("Wallraff"));
        assertThat($(".logo").text(), equalTo("Wallraff"));

        $(".nav-item a", withText("Endpoints")).click();
        Thread.sleep(6100); // why I have to do this?!?

        assertExists($("h3", withText("SendGrid")));
        assertExists($("span", withText("https://api.sendgrid.com")));
        assertExists($("span", withText("https://wallraff.cfapps.io/eapi/sendgrid")));
        assertExists($("span", withText("POST https://wallraff.cfapps.io/eapi/sendgrid/v3/mail/send <data>")));
        assertExists($("span.token-value"));

        String token = $("span.token-value").textContent();

        $(".nav-item a", withText("Emails")).click();
        Thread.sleep(100); // why I have to do this?!?

        Response sendEmailResponse = sendEmailUsingSendgrid(token);

        assertEquals(sendEmailResponse.getStatusCode(), ACCEPTED.value());

        assertExists($("div", withText("From: sendgrid@example.com")));
        assertExists($("div", withText("To: featuretest@example.com, anotherto@example.com")));
        assertExists($("div", withText("Subject: Sending with SendGrid is Fun")));

        $(".email").click();
        Thread.sleep(100); // because await() is broken

        assertExists($("dd", withText("sendgrid@example.com")));
        assertExists($("dd", withText("featuretest@example.com, anotherto@example.com")));
        assertExists($("dd", withText("Sending with SendGrid is Fun")));
        assertExists($(".email-detail--body", withText("B0dy")));
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
        Email from = new Email("sendgrid@example.com");
        String subject = "Sending with SendGrid is Fun";
        Email to = new Email("featuretest@example.com");
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

    private void assertExists(FluentList<FluentWebElement> elements) {
        assertEquals(elements.size(), 1);
    }
}
