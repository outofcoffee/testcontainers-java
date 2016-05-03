package org.testcontainers.images.builder.traits;

import org.testcontainers.containers.ContainerLaunchException;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

/**
 * BuildContextBuilder's trait for classpath-based resources.
 *
 */
public interface ClasspathTrait<SELF extends ClasspathTrait<SELF> & BuildContextBuilderTrait<SELF> & FilesTrait<SELF>> {

    default SELF withFileFromClasspath(String path, String resourcePath) {
        final URL resource = ClasspathTrait.class.getClassLoader().getResource(resourcePath);

        if (resource == null) {
            throw new IllegalArgumentException("Could not find classpath resource at provided path: " + resourcePath);
        }

        try {
            // convert to URI to preserve proper path syntax on Windows
            return ((SELF) this).withFileFromPath(path, Paths.get(resource.toURI()));
        } catch (URISyntaxException e) {
            throw new ContainerLaunchException("Failed to locate classpath resource at provided path: " + resourcePath, e);
        }
    }
}
