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

import static org.fluentlenium.core.filter.FilterConstructor.withText;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.springframework.http.HttpStatus.ACCEPTED;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = WallraffApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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
    public void testEmailAppears() throws Exception {
        goTo("http://" + hostname + ":" + port);

        assertThat(window().title(), equalTo("Wallraff"));
        assertThat($(".logo").text(), equalTo("Wallraff"));

        Response sendEmailResponse = sendEmailUsingSendgrid();

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

    private Response sendEmailUsingSendgrid() throws Exception {
        Email from = new Email("sendgrid@example.com");
        String subject = "Sending with SendGrid is Fun";
        Email to = new Email("featuretest@example.com");
        Content content = new Content("text/plain", "B0dy");
        Mail mail = new Mail(from, subject, to, content);

        Personalization personalization = new Personalization();
        personalization.addTo(new Email("anotherto@example.com"));
        mail.addPersonalization(personalization);

        SendGrid sg = new SendGrid("1234", true);
        sg.setHost(hostname + ":" + port + "/eapi/sendgrid");

        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());
        Response response = sg.api(request);

        return response;
    }

    private void assertExists(FluentList<FluentWebElement> elements) {
        assertEquals(elements.size(), 1);
    }
}
