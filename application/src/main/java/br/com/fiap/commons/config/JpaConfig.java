package br.com.fiap.commons.config;

import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ResourceLoader;
import org.springframework.orm.jpa.persistenceunit.PersistenceManagedTypes;
import org.springframework.orm.jpa.persistenceunit.PersistenceManagedTypesScanner;

@Configuration
@EnableJpaAuditing
@EntityScan("br.com.fiap")
@EnableJpaRepositories("br.com.fiap")
public class JpaConfig {

    @Bean
    public PersistenceManagedTypes persistenceManagedTypes(ResourceLoader resourceLoader) {
        return new PersistenceManagedTypesScanner(resourceLoader)
                .scan("br.com.fiap.garage.domain.entity", "br.com.fiap.commons.entity");
    }
}
