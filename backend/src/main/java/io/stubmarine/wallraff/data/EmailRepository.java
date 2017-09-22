package io.stubmarine.wallraff.data;

import org.springframework.data.repository.CrudRepository;

public interface EmailRepository extends CrudRepository<EmailRecord, Integer> {
    Iterable<EmailRecord> findByInbox(String inbox);
}
