package io.wallraff.data;

import org.springframework.data.repository.CrudRepository;

public interface EmailRepository extends CrudRepository<EmailRecord, Integer> {
}
