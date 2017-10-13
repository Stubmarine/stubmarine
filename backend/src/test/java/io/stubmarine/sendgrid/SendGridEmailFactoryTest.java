package io.stubmarine.sendgrid;

import io.stubmarine.data.EmailRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collection;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@RunWith(MockitoJUnitRunner.class)
public class SendGridEmailFactoryTest {

    @InjectMocks
    private SendGridEmailFactory factory;

    @Test
    public void testGetEmailsFromRequest() throws Exception {
        Collection<EmailRecord> results = factory.getEmailsFromRequest(
                new MailSendForm(
                        "My subjecT",
                        new AddressForm("sender@example.com", null),
                        asList(
                                new PersonalizationForm(
                                        asList(
                                                new AddressForm("to@example.com", null),
                                                new AddressForm("to2@example.com", null)
                                        ),
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

        assertThat(results, hasSize(1));

        EmailRecord result = results.stream().findFirst().get();
        assertThat(result, equalTo(
                new EmailRecord(
                        null,
                        "sender@example.com",
                        "to@example.com, to2@example.com",
                        "",
                        "",
                        "My subjecT",
                        "Content content content!",
                        "zoo"
                )
        ));
    }

    @Test
    public void testGetEmailsFromRequest_CcRecipients() throws Exception {
        Collection<EmailRecord> results = factory.getEmailsFromRequest(
                new MailSendForm(
                        "My subjecT",
                        new AddressForm("sender@example.com", null),
                        asList(
                                new PersonalizationForm(
                                        null,
                                        asList(
                                                new AddressForm("cc@example.com", null),
                                                new AddressForm("cc2@example.com", null)
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

        assertThat(results, hasSize(1));

        EmailRecord result = results.stream().findFirst().get();
        assertThat(result, equalTo(new EmailRecord(
                null,
                "sender@example.com",
                "",
                "cc@example.com, cc2@example.com",
                "",
                "My subjecT",
                "Content content content!",
                "zoo"
        )));
    }

    @Test
    public void testGetEmailsFromRequest_BccRecipients() throws Exception {
        Collection<EmailRecord> results = factory.getEmailsFromRequest(
                new MailSendForm(
                        "My subjecT",
                        new AddressForm("sender@example.com", null),
                        asList(
                                new PersonalizationForm(
                                        null,
                                        null,
                                        asList(
                                                new AddressForm("bcc@example.com", null),
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

        assertThat(results, hasSize(1));

        EmailRecord result = results.stream().findFirst().get();
        assertThat(result, equalTo(new EmailRecord(
                null,
                "sender@example.com",
                "",
                "",
                "bcc@example.com, bcc2@example.com",
                "My subjecT",
                "Content content content!",
                "zoo"
        )));
    }

    @Test
    public void testGetEmailsFromRequest_DisplayNames() throws Exception {
        Collection<EmailRecord> results = factory.getEmailsFromRequest(
                new MailSendForm(
                        "My subjecT",
                        new AddressForm("sender@example.com", "The Sender"),
                        asList(
                                new PersonalizationForm(
                                        asList(new AddressForm("to@example.com", "To Name")),
                                        asList(new AddressForm("cc@example.com", "Ceecee")),
                                        asList(new AddressForm("bcc@example.com", "BeeCC"))
                                )
                        ),
                        asList(
                                new ContentForm("text/plain", "Content content content!")
                        )
                ),
                "zoo"
        );


        assertThat(results, hasSize(1));

        EmailRecord result = results.stream().findFirst().get();
        assertThat(result, equalTo(new EmailRecord(
                null,
                "The Sender <sender@example.com>",
                "To Name <to@example.com>",
                "Ceecee <cc@example.com>",
                "BeeCC <bcc@example.com>",
                "My subjecT",
                "Content content content!",
                "zoo"
        )));
    }

    @Test
    public void testGetEmailsFromRequest_MultiplePersonalizations() throws Exception {
        Collection<EmailRecord> results = factory.getEmailsFromRequest(
                new MailSendForm(
                        "My subjecT",
                        new AddressForm("sender@example.com", null),
                        asList(
                                new PersonalizationForm(
                                        asList(
                                                new AddressForm("to1@example.com", null)
                                        ),
                                        null,
                                        null
                                ),
                                new PersonalizationForm(
                                        asList(
                                                new AddressForm("to2@example.com", null)
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

        assertThat(results, containsInAnyOrder(
                new EmailRecord(
                        null,
                        "sender@example.com",
                        "to1@example.com",
                        "",
                        "",
                        "My subjecT",
                        "Content content content!",
                        "zoo"
                ),
                new EmailRecord(
                        null,
                        "sender@example.com",
                        "to2@example.com",
                        "",
                        "",
                        "My subjecT",
                        "Content content content!",
                        "zoo"
                )
        ));
    }

    @Test
    public void testGetEmailsFromRequest_FiltersByContentType() throws Exception {
        Collection<EmailRecord> results = factory.getEmailsFromRequest(
                new MailSendForm(
                        "My subjecT",
                        new AddressForm("sender@example.com", null),
                        asList(
                                new PersonalizationForm(
                                        asList(
                                                new AddressForm("to@example.com", null)
                                        ),
                                        null,
                                        null
                                )
                        ),
                        asList(
                                new ContentForm("text/html", "<h1>Hello World</h1>"),
                                new ContentForm("text/plain", "Content")
                        )
                ),
                "zoo"
        );

        assertThat(results, hasSize(1));

        EmailRecord result = results.stream().findFirst().get();
        assertThat(result.getBody(), equalTo("Content"));
    }

    @Test
    public void testGetEmailsFromRequest_HandlesNoTextContent() throws Exception {
        Collection<EmailRecord> results = factory.getEmailsFromRequest(
                new MailSendForm(
                        "My subjecT",
                        new AddressForm("sender@example.com", null),
                        asList(
                                new PersonalizationForm(
                                        asList(
                                                new AddressForm("to@example.com", null)
                                        ),
                                        null,
                                        null
                                )
                        ),
                        asList(
                                new ContentForm("text/html", "<h1>Hello World</h1>")
                        )
                ),
                "zoo"
        );

        assertThat(results, hasSize(1));

        EmailRecord result = results.stream().findFirst().get();
        assertThat(result.getBody(), equalTo(""));
    }
}