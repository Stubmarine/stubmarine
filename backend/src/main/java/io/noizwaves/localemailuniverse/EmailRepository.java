package io.noizwaves.localemailuniverse;

import org.springframework.data.repository.CrudRepository;

public interface EmailRepository extends CrudRepository<EmailRecord, Integer> {
}
