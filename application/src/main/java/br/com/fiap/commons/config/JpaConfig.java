package br.com.fiap.commons.config;

import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaAuditing
@EntityScan("br.com.fiap")
@EnableJpaRepositories("br.com.fiap")
public class JpaConfig {

}
