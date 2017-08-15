package io.noizwaves.localemailuniverse.api;

import io.noizwaves.localemailuniverse.data.EmailRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

@RestController
public class EmailController {

    private final EmailRepository emailRepository;

    public EmailController(EmailRepository emailRepository) {
        this.emailRepository = emailRepository;
    }

    @RequestMapping("/api/emails")
    public List list() {
        return asStream(emailRepository.findAll())
                .map(EmailListItem::new)
                .collect(toList());
    }

    private static <T> Stream<T> asStream(Iterable<T> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false);
    }
}
