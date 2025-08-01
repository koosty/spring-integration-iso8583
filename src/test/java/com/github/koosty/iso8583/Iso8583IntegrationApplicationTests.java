package com.github.koosty.iso8583;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.GenericPackager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.ip.tcp.TcpOutboundGateway;
import org.springframework.integration.ip.tcp.connection.AbstractClientConnectionFactory;
import org.springframework.integration.ip.tcp.connection.TcpNetClientConnectionFactory;
import org.springframework.integration.ip.tcp.serializer.ByteArrayCrLfSerializer;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for the ISO 8583 Spring Boot application.
 * <p>
 * This test class verifies the end-to-end integration of ISO 8583 message handling
 * over TCP using Spring Integration and JPOS. It sends a sample ISO 8583 request
 * and validates the response from the server.
 * </p>
 */
@SpringBootTest
class Iso8583IntegrationApplicationTests {
    /**
     * Gateway for sending ISO 8583 messages to the TCP server.
     */
    @Autowired
    private TcpClientConfiguration.TcpClientGateway tcpClientGateway;
    /**
     * The ISO 8583 packager used for packing and unpacking messages in tests.
     */
    @Autowired
    private GenericPackager packager;

    /**
     * Verifies that the Spring context loads and the ISO 8583 integration works as expected.
     *
     * @throws ISOException if there is an error packing or unpacking the ISO 8583 message
     */
    @Test
    void contextLoads() throws ISOException {
        // create a new ISOMsg instance with the packager
        ISOMsg request = new ISOMsg();
        request.setPackager(packager);

        // set MTI as financial
        request.setMTI("0100");

        // set data fields
        request.set(2, "5642570404782927");
        request.set(3, "011000");
        request.set(4, "78000");
        request.set(7, "1220145711");
        request.set(11, "101183");
        request.set(12, "145711");
        request.set(13, "1220");
        request.set(14, "2408");
        request.set(15, "1220");
        request.set(18, "6011");
        request.set(22, "051");
        request.set(25, "00");
        request.set(26, "04");
        request.set(28, "C00000000");
        request.set(30, "C00000000");
        request.set(32, "56445700");
        request.set(37, "567134101183");
        request.set(41, "N1742");
        request.set(42, "ATM004");
        request.set(43, "45 SR LEDERSHIP DUABANAT NUEVA ECIJAQ PH");
        request.set(49, "608");
        request.set(102, "970630181070041");
        request.set(120, "BRN015301213230443463");
        Map<String, String> f172022 = new LinkedHashMap<>();
        f172022.put("MSDN", "2260953");
        f172022.put("UssdSessionId", "mtn:260962210258:298a2003-cc04-4d13-8947-a0435a3d9205");
        f172022.put("SENDER_FULL_NAME", "John Mostert");
        request.set("127.022", PostilionUtils.buildF172022(f172022));
        // Send the request to the TCP server and receive the response
        byte[] byteResponse = tcpClientGateway.send(request.pack());
        // unpack the response into an ISOMsg
        ISOMsg response = new ISOMsg();
        response.setPackager(packager);
        response.unpack(byteResponse);
        // Check that the response has the expected MTI
        assertThat("0100").isEqualTo(response.getMTI());
    }

    /**
     * Test configuration for setting up TCP client components.
     * <p>
     * This configuration provides the necessary beans for creating a TCP client
     * that can communicate with the ISO 8583 server during integration testing.
     * </p>
     */
    @TestConfiguration
    public static class TcpClientConfiguration {

        /**
         * The name of the message channel used for outbound client messages.
         */
        private static final String MESSAGE_CHANNEL = "client-message-channel";

        /**
         * Creates and configures the client connection factory for TCP connections.
         * <p>
         * The factory is configured to connect to localhost on port 2222 with
         * STX/ETX serialization and single-use connections.
         * </p>
         *
         * @return the configured AbstractClientConnectionFactory bean
         */
        @Bean
        public AbstractClientConnectionFactory clientFactory() {
            AbstractClientConnectionFactory factory = new TcpNetClientConnectionFactory("localhost", 2222);
            factory.setSerializer(new ByteArrayCrLfSerializer());
            factory.setDeserializer(new ByteArrayCrLfSerializer());
            factory.setSingleUse(true);
            return factory;
        }

        /**
         * Creates and configures the TCP outbound gateway for sending messages to the server.
         * <p>
         * The gateway is configured to use the client connection factory and requires
         * a reply for each message sent.
         * </p>
         *
         * @param clientFactory the client connection factory to use
         * @return the configured TcpOutboundGateway bean
         */
        @Bean
        @ServiceActivator(inputChannel = MESSAGE_CHANNEL)
        public TcpOutboundGateway outboundGateway(AbstractClientConnectionFactory clientFactory) {
            TcpOutboundGateway outboundGateway = new TcpOutboundGateway();
            outboundGateway.setConnectionFactory(clientFactory);
            outboundGateway.setLoggingEnabled(true);
            outboundGateway.setRequiresReply(true);

            return outboundGateway;
        }

        /**
         * Gateway interface for sending byte arrays to the TCP server.
         * <p>
         * This messaging gateway provides a simple interface for sending
         * ISO 8583 message payloads to the server and receiving responses.
         * </p>
         */
        @MessagingGateway
        public interface TcpClientGateway {
            /**
             * Sends a byte array payload to the TCP server and returns the response.
             *
             * @param payload the byte array to send to the server
             * @return the response from the server as a byte array
             */
            @Gateway(requestChannel = MESSAGE_CHANNEL)
            byte[] send(byte[] payload);
        }
    }
}
