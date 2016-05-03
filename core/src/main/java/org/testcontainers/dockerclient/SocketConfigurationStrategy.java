package org.testcontainers.dockerclient;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import org.jetbrains.annotations.Nullable;

/**
 * Attempt to configure docker using a socket.
 *
 * @author richardnorth
 * @author pcornish
 */
public class SocketConfigurationStrategy implements DockerConfigurationStrategy {
    /**
     * Location of the socket.
     */
    private final String socketLocation;

    /**
     * Explicitly set the docker host.
     */
    @Nullable
    private final String dockerHostOverride;

    /**
     * Human readable description of the socket.
     */
    private final String socketDescription;

    /**
     * @param socketDescription the human readable description of the socket
     * @param socketLocation    the location of the socket, such as {@literal http://path/to/socket}
     */
    public SocketConfigurationStrategy(String socketDescription, String socketLocation) {
        this(socketDescription, socketLocation, null);
    }

    /**
     * @param socketDescription  the human readable description of the socket
     * @param socketLocation     the location of the socket, such as {@literal http://path/to/socket}
     * @param dockerHostOverride explicitly set the docker host
     */
    public SocketConfigurationStrategy(String socketDescription, String socketLocation, @Nullable String dockerHostOverride) {
        this.socketLocation = socketLocation;
        this.dockerHostOverride = dockerHostOverride;
        this.socketDescription = socketDescription;
    }

    @Override
    public DockerClientConfig provideConfiguration() throws InvalidConfigurationException {
        final DockerClientConfig config = new DockerClientConfig.DockerClientConfigBuilder().withDockerHost(socketLocation).build();
        final DockerClient client = DockerClientBuilder.getInstance(config).build();

        try {
            client.pingCmd().exec();
        } catch (Exception e) {
            throw new InvalidConfigurationException("ping failed", e);
        }

        LOGGER.info("Accessing docker with {} socket", socketDescription);
        return config;
    }

    @Override
    public String getDescription() {
        return socketDescription + " socket (" + socketLocation + ")";
    }

    /**
     * Return the host of the configured docker instance, or {@link #dockerHostOverride}, if non-{@code null}.
     *
     * @param config the active configuration
     * @return the docker host
     */
    @Override
    public String getDockerHostAddress(DockerClientConfig config) {
        return (null != dockerHostOverride ? dockerHostOverride : DockerConfigurationStrategy.super.getDockerHostAddress(config));
    }
}
