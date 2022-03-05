package com.cloud.custom.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cloud.config.environment.Environment;
import org.springframework.cloud.config.environment.PropertySource;
import org.springframework.cloud.config.server.environment.EnvironmentRepository;
import org.springframework.cloud.config.server.environment.SearchPathCompositeEnvironmentRepository;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CustomCompositeEnvironmentRepository extends SearchPathCompositeEnvironmentRepository {

    Log log = LogFactory.getLog(getClass());

    protected List<EnvironmentRepository> environmentRepositories;

    private boolean failOnError;

    /**
     * Creates a new {@link CustomCompositeEnvironmentRepository}.
     * @param environmentRepositories The list of {@link EnvironmentRepository}s to create
     * the composite from.
     * @param failOnError whether to throw an exception if there is an error.
     */
    public CustomCompositeEnvironmentRepository(List<EnvironmentRepository> environmentRepositories, boolean failOnError) {
        super(environmentRepositories, failOnError);
    }

    @Override
    public Environment findOne(String application, String profile, String label) {
        Environment env = super.findOne(application, profile, label, false);
        return mergePropertySource(env);
    }

    @Override
    public Environment findOne(String application, String profile, String label, boolean includeOrigin) {
        Environment env = super.findOne(application, profile, label, includeOrigin);
        return mergePropertySource(env);
    }

    private Environment mergePropertySource(Environment environment) {
        if(null != environment.getPropertySources() && !environment.getPropertySources().isEmpty()){
            List<PropertySource> propertySources = environment.getPropertySources();
            Collections.reverse(propertySources);
            Map<String, String> sourceMap = new LinkedHashMap<>();
            propertySources.forEach(propSource -> {
                if(null != propSource.getSource() && !propSource.getSource().isEmpty()){
                    propSource.getSource().forEach((propName, propVal) -> sourceMap.put(String.valueOf(propName), String.valueOf(propVal)));
                }
            });
            String name = StringUtils.hasText(environment.getName()) ? environment.getName() : "";
            String profile = StringUtils.hasText(String.join(",", environment.getProfiles())) ? String.join(",", environment.getProfiles()) : "";
            var propertySource = new PropertySource(name +"-"+profile, sourceMap);
            environment.getPropertySources().clear();
            environment.getPropertySources().add(propertySource);
        }
        return environment;
    }
}
