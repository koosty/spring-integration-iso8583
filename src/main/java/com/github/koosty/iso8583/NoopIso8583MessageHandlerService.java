package com.github.koosty.iso8583;

import org.jpos.iso.ISOMsg;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * No-op implementation of the {@link Iso8385MessageHandlerService}.
 */
@Service
public class NoopIso8583MessageHandlerService implements Iso8385MessageHandlerService{
    /**
     * This is a no-op implementation that simply returns the message as is.
     * @param message the ISO message to handle
     * @return the same message that was passed in
     */
    @Override
    public ISOMsg handleMessage(ISOMsg message) {
        System.out.println("Handling message: ");
        message.dump(System.out, "");
        String string172022 = message.getString("127.022");
        Map<String, String> f172022 = PostilionUtils.parseF172022(string172022);
        System.out.println("Identified Field 127.022: " + message.getString("127.022"));
        System.out.println("Parsed Field 127.022: " + f172022);

        return message;
    }

    /**
     * This is a no-op implementation that always returns true.
     * Actual implementations should check if the message can be handled
     * <code>
     *     return "0100".equals(message.getMTI()) && "1".equals(message.getString("127.022.1"));
     * </code>
     * @param message the ISO message to check
     * @return true if the message can be handled, false otherwise
     */
    @Override
    public boolean canHandle(ISOMsg message){
        return true;
    }
}
