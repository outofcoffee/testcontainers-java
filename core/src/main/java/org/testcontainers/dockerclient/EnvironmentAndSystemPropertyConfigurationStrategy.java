package org.testcontainers.dockerclient;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;

/**
 * Use environment variables and system properties (as supported by the underlying DockerClient DefaultConfigBuilder)
 * to try and locate a docker environment.
 */
public class EnvironmentAndSystemPropertyConfigurationStrategy implements DockerConfigurationStrategy {

    private DockerClientConfig config = DockerClientConfig.createDefaultConfigBuilder().build();

    @Override
    public DockerClientConfig provideConfiguration() throws InvalidConfigurationException {
        // Try using environment variables
        DockerClientConfig candidateConfig = config;
        DockerClient client = DockerClientBuilder.getInstance(candidateConfig).build();

        try {
            client.pingCmd().exec();
        } catch (Exception e) {
            throw new InvalidConfigurationException("ping failed");
        }

        LOGGER.info("Found docker client settings from environment");
        LOGGER.info("Docker host IP address is {}", DockerClientConfigUtils.getDockerHostIpAddress(candidateConfig));

        return candidateConfig;
    }

    @Override
    public String getDescription() {
        return "Environment variables, system properties and defaults. Resolved: \n" + stringRepresentation(config);
    }

    private String stringRepresentation(DockerClientConfig config) {
        return  "    uri=" + config.getDockerHost() + "\n" +
                "    sslConfig='" + config.getDockerTlsVerify() + "'\n" +
                "    version='" + config.getApiVersion() + "'\n" +
                "    username='" + config.getAuthConfigurations().getConfigs().get(0).getUsername() + "'\n" +
                "    password='" + config.getAuthConfigurations().getConfigs().get(0).getPassword() + "'\n" +
                "    email='" + config.getAuthConfigurations().getConfigs().get(0).getEmail() + "'\n" +
                "    serverAddress='" + config.getRegistryUrl() + "'\n" +
                "    dockerCfgPath='" + config.getDockerCertPath() + "'\n";
    }
}
