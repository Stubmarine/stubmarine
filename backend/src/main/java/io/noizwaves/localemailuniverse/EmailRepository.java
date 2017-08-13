package io.noizwaves.localemailuniverse;

import org.springframework.stereotype.Repository;

import java.util.List;

import static java.util.Arrays.asList;

@Repository
public class EmailRepository {
    private static final List<EmailRecord> emails = asList(
            new EmailRecord(1, "Foo <foo@example.com>", "Bar <bar@example.com>", "Hello World!", "And they said hello world! The end. - Bar"),
            new EmailRecord(2, "Bar <bar@example.com>", "Foo <foo@example.com>", "RE: Hello World!", "Thanks for that... Foo")
    );

    public List<EmailRecord> findAll() {
        return emails;
    }
}
