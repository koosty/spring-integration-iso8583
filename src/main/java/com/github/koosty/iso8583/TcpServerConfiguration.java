package com.github.koosty.iso8583;

import org.jpos.iso.ISOException;
import org.jpos.iso.packager.GenericPackager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.integration.ip.tcp.TcpInboundGateway;
import org.springframework.integration.ip.tcp.connection.AbstractServerConnectionFactory;
import org.springframework.integration.ip.tcp.connection.TcpNetServerConnectionFactory;
import org.springframework.integration.ip.tcp.serializer.ByteArrayCrLfSerializer;
import org.springframework.integration.ip.tcp.serializer.ByteArrayStxEtxSerializer;

import java.io.IOException;
import java.io.InputStream;

/**
 * Configuration class for the TCP server and ISO 8583 integration.
 * <p>
 * This class sets up the TCP server, message channel, and ISO 8583 packager beans
 * required for handling inbound ISO 8583 messages over TCP.
 * </p>
 */
@Configuration
public class TcpServerConfiguration {
    /**
     * The name of the message channel used for inbound ISO 8583 messages.
     */
    public static final String MESSAGE_CHANNEL = "message-channel";
    private final ServerProperties serverProperties;
    private final ResourceLoader resourceLoader;

    /**
     * Constructs a new TcpServerConfiguration with the specified server properties and resource loader.
     *
     * @param serverProperties the server properties configuration
     * @param resourceLoader the resource loader for loading resources
     */
    public TcpServerConfiguration(ServerProperties serverProperties, ResourceLoader resourceLoader) {
        this.serverProperties = serverProperties;
        this.resourceLoader = resourceLoader;
    }

    /**
     * Creates and configures the server connection factory bean for TCP connections.
     *
     * @return the configured AbstractServerConnectionFactory bean
     */
    @Bean
    public AbstractServerConnectionFactory serverFactory() {
        AbstractServerConnectionFactory factory = new TcpNetServerConnectionFactory(serverProperties.getPort());
        factory.setSerializer(new ByteArrayCrLfSerializer());
        factory.setDeserializer(new ByteArrayCrLfSerializer());

        return factory;
    }

    /**
     * Creates and configures a {@link TcpInboundGateway} bean.
     * <p>
     * The inbound gateway listens for incoming TCP connections using the provided
     * {@link AbstractServerConnectionFactory} and routes messages to the configured
     * request channel.
     * </p>
     *
     * @param serverFactory the server connection factory to use
     * @return the configured TcpInboundGateway bean
     */
    @Bean
    public TcpInboundGateway inboundGateway(AbstractServerConnectionFactory serverFactory) {
        TcpInboundGateway inbound = new TcpInboundGateway();
        inbound.setConnectionFactory(serverFactory);
        inbound.setRequestChannelName(MESSAGE_CHANNEL);
        inbound.setLoggingEnabled(true);
        return inbound;
    }

    /**
     * Creates a {@link GenericPackager} bean for ISO 8583 message parsing.
     * <p>
     * Loads the ISO 8583 field definitions from the classpath resource
     * <code>iso-8583-fields.xml</code>.
     * </p>
     *
     * @return the configured GenericPackager bean
     * @throws IOException if the resource cannot be read
     * @throws ISOException if the packager cannot be created
     */
    @Bean
    public GenericPackager iso8583Packager() throws IOException, ISOException {
        InputStream inputStream = resourceLoader.getResource("classpath:iso-8583-fields.xml").getInputStream();
        return new GenericPackager(inputStream);
    }
}
