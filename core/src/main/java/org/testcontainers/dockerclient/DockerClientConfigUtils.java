package org.testcontainers.dockerclient;

import com.github.dockerjava.core.DockerClientConfig;
import org.testcontainers.DockerClientFactory;

public class DockerClientConfigUtils {
    public static String getDockerHostIpAddress(DockerClientConfig config) {
        return DockerClientFactory.instance().getDockerHostAddress(config);
    }
}
