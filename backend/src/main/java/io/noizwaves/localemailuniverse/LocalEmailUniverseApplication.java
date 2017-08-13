package io.noizwaves.localemailuniverse;

import io.noizwaves.localemailuniverse.data.EmailRecord;
import io.noizwaves.localemailuniverse.data.EmailRepository;
import io.noizwaves.localemailuniverse.smtp.EmailRepositoryMessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.subethamail.smtp.helper.SimpleMessageListenerAdapter;
import org.subethamail.smtp.server.SMTPServer;

import static java.util.Arrays.asList;

@SpringBootApplication
public class LocalEmailUniverseApplication {
	private static final Logger log = LoggerFactory.getLogger(LocalEmailUniverseApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(LocalEmailUniverseApplication.class, args);
	}

	@Bean
    public SMTPServer smtpServer(EmailRepositoryMessageListener listener) {
        SMTPServer smtpServer = new SMTPServer(new SimpleMessageListenerAdapter(listener));
        smtpServer.setPort(8081);
        smtpServer.start();

        return smtpServer;
    }

	@Bean
	@Profile("default")
	public CommandLineRunner demo(EmailRepository emailRepository) {
		return (args) -> {
			emailRepository.save(asList(
					new EmailRecord(1, "Foo <foo@example.com>", "Bar <bar@example.com>", "Hello World!", "And they said hello world! The end. - Bar"),
					new EmailRecord(2, "Bar <bar@example.com>", "Foo <foo@example.com>", "RE: Hello World!", "Thanks for that... Foo")
			));

			log.info("Finished loading seed data");
		};
	}


}
