package com.cloud.custom.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cloud.config.environment.Environment;
import org.springframework.cloud.config.server.environment.CompositeEnvironmentRepository;
import org.springframework.cloud.config.server.environment.EnvironmentRepository;
import org.springframework.core.OrderComparator;

import java.util.Collections;
import java.util.List;

public class CustomCompositeEnvironmentRepository implements EnvironmentRepository {

    Log log = LogFactory.getLog(getClass());

    protected List<EnvironmentRepository> environmentRepositories;

    private boolean failOnError;

    /**
     * Creates a new {@link CompositeEnvironmentRepository}.
     * @param environmentRepositories The list of {@link EnvironmentRepository}s to create
     * the composite from.
     * @param failOnError whether to throw an exception if there is an error.
     */
    public CustomCompositeEnvironmentRepository(List<EnvironmentRepository> environmentRepositories, boolean failOnError) {
        // Sort the environment repositories by the priority
        Collections.sort(environmentRepositories, OrderComparator.INSTANCE);
        this.environmentRepositories = environmentRepositories;
        this.failOnError = failOnError;
    }

    @Override
    public Environment findOne(String application, String profile, String label) {
        return findOne(application, profile, label, false);
    }

    @Override
    public Environment findOne(String application, String profile, String label, boolean includeOrigin) {
        Environment env = new Environment(application, new String[] { profile }, label, null, null);
        if (this.environmentRepositories.size() == 1) {
            Environment envRepo = this.environmentRepositories.get(0).findOne(application, profile, label,
                    includeOrigin);
            env.addAll(envRepo.getPropertySources());
            env.setVersion(envRepo.getVersion());
            env.setState(envRepo.getState());
        }
        else {
            for (EnvironmentRepository repo : environmentRepositories) {
                try {
                    env.addAll(repo.findOne(application, profile, label, includeOrigin).getPropertySources());
                }
                catch (Exception e) {
                    if (failOnError) {
                        throw e;
                    }
                    else {
                        log.info("Error adding environment for " + repo);
                    }
                }
            }
        }
        return env;
    }
}
