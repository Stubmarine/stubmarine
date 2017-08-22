package io.wallraff;

import org.fluentlenium.adapter.junit.FluentTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class HappyPathTest extends FluentTest {

    private ConfigurableApplicationContext applicationContext;

    @Before
    public void before() throws Exception {
        applicationContext = SpringApplication.run(WallraffApplication.class, "--server.port=9292");
    }

    @After
    public void after() throws Exception {
        applicationContext.close();
    }

    @Test()
    public void testAppLoads() {
        goTo("http://localhost:9292");

        assertThat(window().title(), equalTo("Wallraff"));

        assertThat($(".logo").text(), equalTo("Wallraff"));
    }
}
