package br.com.fiap.commons.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@TestConfiguration
@EnableWebMvc
@ComponentScan(basePackages = "br.com.fiap.commons.advice")
@Import({
        GsonConfig.class,
        AopTestConfig.class,
})
public class RestControllerTestConfig {

}
