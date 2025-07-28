package com.github.koosty.iso8583;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for the TCP server.
 * <p>
 * This class binds to properties prefixed with <code>server</code> in the application's configuration files.
 * It is used to configure the server port for the ISO 8583 integration service.
 * </p>
 */
@Component
@ConfigurationProperties(prefix = "server")
public class ServerProperties {
    /**
     * The port on which the TCP server will listen. Default is 2222.
     */
    private int port = 2222;

    /**
     * Gets the configured server port.
     *
     * @return the server port
     */
    public int getPort() {
        return port;
    }

    /**
     * Sets the server port.
     *
     * @param port the port to set
     */
    public void setPort(int port) {
        this.port = port;
    }
}
