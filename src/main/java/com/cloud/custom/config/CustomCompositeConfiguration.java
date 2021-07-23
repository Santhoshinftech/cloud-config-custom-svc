package com.cloud.custom.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.config.server.config.ConfigServerProperties;
import org.springframework.cloud.config.server.config.EnvironmentRepositoryConfiguration;
import org.springframework.cloud.config.server.environment.CompositeEnvironmentRepository;
import org.springframework.cloud.config.server.environment.EnvironmentRepository;
import org.springframework.cloud.config.server.environment.SearchPathCompositeEnvironmentRepository;
import org.springframework.cloud.config.server.environment.SearchPathLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.ArrayList;
import java.util.List;

@Configuration
@AutoConfigureBefore(EnvironmentRepositoryConfiguration.class)
public class CustomCompositeConfiguration {

    private List<EnvironmentRepository> environmentRepos = new ArrayList<>();

    private ConfigServerProperties properties;

    @Bean
    @Primary
    @ConditionalOnMissingBean(SearchPathLocator.class)
    public CompositeEnvironmentRepository customCompositeEnvironmentRepository() {
        return new CustomCompositeEnvironmentRepository(this.environmentRepos, properties.isFailOnCompositeError());
    }

    @Autowired
    public void setEnvironmentRepos(List<EnvironmentRepository> repos) {
        this.environmentRepos = repos;
    }

    @Autowired
    public void setProperties(ConfigServerProperties properties) {
        this.properties = properties;
    }
}
