package io.stubmarine.wallraff;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WallraffApplication {
    private static final Logger log = LoggerFactory.getLogger(WallraffApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(WallraffApplication.class, args);
    }
}
