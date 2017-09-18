package io.wallraff.sendgrid;

import io.wallraff.data.EmailRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@RunWith(MockitoJUnitRunner.class)
public class SendGridEmailFactoryTest {

    @InjectMocks
    private SendGridEmailFactory factory;

    @Test
    public void testGetEmailFromRequest() throws Exception {
        EmailRecord result = factory.getEmailFromRequest(
                new MailSendForm(
                        "My subjecT",
                        new AddressForm("sender@example.com", null),
                        asList(
                                new PersonalizationForm(
                                        asList(new AddressForm("to@example.com", null)),
                                        emptyList(),
                                        emptyList()
                                )
                        ),
                        asList(
                                new ContentForm("text/plain", "Content content content!")
                        )
                ),
                "zoo"
        );


        assertThat(result, equalTo(
                new EmailRecord(
                        null,
                        "sender@example.com",
                        "to@example.com",
                        "",
                        "",
                        "My subjecT",
                        "Content content content!",
                        "zoo"
                )
        ));
    }

    @Test
    public void testGetEmailFromRequest_MultipleRecipients() throws Exception {
        EmailRecord result = factory.getEmailFromRequest(
                new MailSendForm(
                        "My subjecT",
                        new AddressForm("sender@example.com", null),
                        asList(
                                new PersonalizationForm(
                                        asList(
                                                new AddressForm("to1@example.com", null),
                                                new AddressForm("to2@example.com", null)
                                        ),
                                        null,
                                        null
                                ),
                                new PersonalizationForm(
                                        asList(
                                                new AddressForm("to3@example.com", null)
                                        ),
                                        null,
                                        null
                                )
                        ),
                        asList(
                                new ContentForm("text/plain", "Content content content!")
                        )
                ),
                "zoo"
        );

        assertThat(result, equalTo(new EmailRecord(
                null,
                "sender@example.com",
                "to1@example.com, to2@example.com, to3@example.com",
                "",
                "",
                "My subjecT",
                "Content content content!",
                "zoo"
        )));
    }

    @Test
    public void testGetEmailFromRequest_CcRecipients() throws Exception {
        EmailRecord result = factory.getEmailFromRequest(
                new MailSendForm(
                        "My subjecT",
                        new AddressForm("sender@example.com", null),
                        asList(
                                new PersonalizationForm(
                                        null,
                                        asList(
                                                new AddressForm("cc@example.com", null),
                                                new AddressForm("cc2@example.com", "Cecetwo")
                                        ),
                                        null
                                )
                        ),
                        asList(
                                new ContentForm("text/plain", "Content content content!")
                        )
                ),
                "zoo"
        );

        assertThat(result, equalTo(new EmailRecord(
                null,
                "sender@example.com",
                "",
                "cc@example.com, Cecetwo <cc2@example.com>",
                "",
                "My subjecT",
                "Content content content!",
                "zoo"
        )));
    }

    @Test
    public void testGetEmailFromRequest_BccRecipients() throws Exception {
        EmailRecord result = factory.getEmailFromRequest(
                new MailSendForm(
                        "My subjecT",
                        new AddressForm("sender@example.com", null),
                        asList(
                                new PersonalizationForm(
                                        null,
                                        null,
                                        asList(
                                                new AddressForm("bcc@example.com", "BeeCC"),
                                                new AddressForm("bcc2@example.com", null)
                                        )
                                )
                        ),
                        asList(
                                new ContentForm("text/plain", "Content content content!")
                        )
                ),
                "zoo"
        );

        assertThat(result, equalTo(new EmailRecord(
                null,
                "sender@example.com",
                "",
                "",
                "BeeCC <bcc@example.com>, bcc2@example.com",
                "My subjecT",
                "Content content content!",
                "zoo"
        )));
    }

    @Test
    public void testGetEmailFromRequest_DisplayNames() throws Exception {
        EmailRecord result = factory.getEmailFromRequest(
                new MailSendForm(
                        "My subjecT",
                        new AddressForm("sender@example.com", "The Sender"),
                        asList(
                                new PersonalizationForm(
                                        asList(
                                                new AddressForm("to1@example.com", "To Name"),
                                                new AddressForm("to2@example.com", null)
                                        ),
                                        null,
                                        null
                                ),
                                new PersonalizationForm(
                                        asList(
                                                new AddressForm("to3@example.com", "Another")
                                        ),
                                        null,
                                        null
                                )
                        ),
                        asList(
                                new ContentForm("text/plain", "Content content content!")
                        )
                ),
                "zoo"
        );

        assertThat(result, equalTo(new EmailRecord(
                null,
                "The Sender <sender@example.com>",
                "To Name <to1@example.com>, to2@example.com, Another <to3@example.com>",
                "",
                "",
                "My subjecT",
                "Content content content!",
                "zoo"
        )));
    }
}