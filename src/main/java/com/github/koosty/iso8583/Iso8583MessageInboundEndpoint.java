package com.github.koosty.iso8583;

import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.GenericPackager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.ip.IpHeaders;
import org.springframework.messaging.Message;

import java.nio.charset.StandardCharsets;

/**
 * Message endpoint for handling inbound ISO 8583 messages.
 * <p>
 * This class receives ISO 8583 messages from a TCP server channel,
 * unpacks them using a provided {@link GenericPackager}, and processes them.
 * </p>
 */
@MessageEndpoint
public class Iso8583MessageInboundEndpoint {
    private static final Logger log = LoggerFactory.getLogger(Iso8583MessageInboundEndpoint.class);
    private final GenericPackager genericPackager;

    /**
     * Constructs a new Iso8583MessageInboundEndpoint with the specified packager.
     *
     * @param genericPackager the ISO 8583 packager to use for message parsing
     */
    public Iso8583MessageInboundEndpoint(GenericPackager genericPackager) {
        this.genericPackager = genericPackager;
    }

    /**
     * Handles incoming ISO 8583 messages from the TCP server channel.
     * <p>
     * This method is invoked by Spring Integration when a message is received
     * on the configured input channel. It logs the connection ID and prepares
     * the message for further processing.
     * </p>
     *
     * @param message the incoming message containing the ISO 8583 payload
     * @return a byte array response to be sent back to the client
     */
    @ServiceActivator(inputChannel = TcpServerConfiguration.MESSAGE_CHANNEL, requiresReply = "true")
    public byte[] onMessage(Message<byte[]> message) {
        log.debug("received message with connection id {}", message.getHeaders().get(IpHeaders.CONNECTION_ID));

        byte[] bytePayload = message.getPayload();
        ISOMsg request = new ISOMsg();
        request.setPackager(genericPackager);
        try {
            // Unpack the ISO message from the byte payload
            request.unpack(bytePayload);
            log.debug("Receive unpacked ISO message: {}", new String(request.pack(), StandardCharsets.UTF_8));
            System.out.println(request.getString("127.022.1")); // this is empty
            // Just return the request as response for demonstration purposes with MTI "0110"
            request.setMTI("0110");
            request.set(11, "000013");
            request.set(39, "00");
            return request.pack();
        } catch (Exception e) {
            log.error("Error unpacking ISO message", e);
            throw new RuntimeException("Failed to unpack ISO message", e);
        }
    }
}
