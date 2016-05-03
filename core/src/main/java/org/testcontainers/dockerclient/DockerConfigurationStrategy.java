package org.testcontainers.dockerclient;

import com.github.dockerjava.core.DockerClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Mechanism to find a viable Docker client configuration according to the host system environment.
 */
public interface DockerConfigurationStrategy {

    /**
     * @return a usable, tested, Docker client configuration for the host system environment
     * @throws InvalidConfigurationException if this strategy fails
     */
    DockerClientConfig provideConfiguration() throws InvalidConfigurationException;

    /**
     * @return a short textual description of the strategy
     */
    String getDescription();

    Logger LOGGER = LoggerFactory.getLogger(DockerConfigurationStrategy.class);

    /**
     * Determine the right DockerClientConfig to use for building clients by trial-and-error.
     *
     * @return a working DockerClientConfig, as determined by successful execution of a ping command
     */
    static DockerClientConfigResult getFirstValidConfig(List<DockerConfigurationStrategy> strategies) {
        Map<DockerConfigurationStrategy, Exception> configurationFailures = new LinkedHashMap<>();

        for (DockerConfigurationStrategy strategy : strategies) {
            try {
                LOGGER.info("Looking for Docker environment. Trying {}", strategy.getDescription());
                return new DockerClientConfigResult(strategy);
            } catch (Exception e) {
                configurationFailures.put(strategy, e);
                LOGGER.debug("Docker strategy " + strategy.getClass().getName() + " failed with exception", e);
            }
        }

        LOGGER.error("Could not find a valid Docker environment. Please check configuration. Attempted configurations were:");
        for (Map.Entry<DockerConfigurationStrategy, Exception> entry : configurationFailures.entrySet()) {
            LOGGER.error("    {}: failed with exception message: {}", entry.getKey().getDescription(), entry.getValue().getMessage());
        }
        LOGGER.error("As no valid configuration was found, execution cannot continue");

        throw new IllegalStateException("Could not find a valid Docker environment. Please see logs and check configuration");
    }

    /**
     * @param config the active configuration
     * @return the host address for the configured docker instance
     */
    default String getDockerHostAddress(DockerClientConfig config) {
        switch (config.getDockerHost().getScheme()) {
            case "http":
            case "https":
            case "tcp":
                return config.getDockerHost().getHost();
            case "unix":
                return "localhost";
            default:
                return null;
        }
    }

    class InvalidConfigurationException extends RuntimeException {

        public InvalidConfigurationException(String s) {
            super(s);
        }

        public InvalidConfigurationException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * Represents a successful configuration result.
     */
    class DockerClientConfigResult {
        private final DockerClientConfig config;
        private final DockerConfigurationStrategy strategy;

        DockerClientConfigResult(DockerConfigurationStrategy strategy) {
            this.config = strategy.provideConfiguration();
            this.strategy = strategy;
        }

        public DockerClientConfig getConfig() {
            return config;
        }

        public DockerConfigurationStrategy getStrategy() {
            return strategy;
        }
    }
}
