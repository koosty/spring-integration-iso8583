package com.github.koosty.iso8583;

import org.jpos.iso.ISOMsg;

public interface Iso8385MessageHandlerService {
    ISOMsg handleMessage(ISOMsg message);
    boolean canHandle(ISOMsg message);
}
