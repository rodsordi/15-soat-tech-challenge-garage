package br.com.fiap.commons.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

@TestConfiguration
@EnableAsync
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ComponentScan(basePackages = "br.com.fiap.commons.aspect")
public class AopTestConfig {

}
