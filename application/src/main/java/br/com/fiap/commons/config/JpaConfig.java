package br.com.fiap.commons.config;

import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaAuditing
@EntityScan(basePackages = {
        "br.com.fiap.garage.domain.entity",
        "br.com.fiap.commons.entity"
})
@EnableJpaRepositories(basePackages = {
        "br.com.fiap.garage.domain.repository",
        "br.com.fiap.garage.application.adapter.repository"
})
public class JpaConfig {

}
