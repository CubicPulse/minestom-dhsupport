package com.cubicpulse.minestom.dhsupport.network.packets;

import com.cubicpulse.minestom.dhsupport.network.NetworkTypes;
import net.minestom.server.network.NetworkBuffer;
import net.minestom.server.network.NetworkBufferTemplate;

public record DHExceptionMessage(int tracker, int typeId, String message) {
    
    public static int TYPE_RATE_LIMITED = 0;
    public static int TYPE_REQUEST_OUT_OF_RANGE = 1;
    public static int TYPE_REQUEST_REJECTED = 2;
    public static int TYPE_SECTION_REQUIRES_SPLITTING = 3;
    
    public static final NetworkBuffer.Type<DHExceptionMessage> SERIALIZER = NetworkBufferTemplate.template(
            NetworkBuffer.INT, DHExceptionMessage::tracker,
            NetworkBuffer.INT, DHExceptionMessage::typeId,
            NetworkTypes.SHORT_STRING, DHExceptionMessage::message,
            DHExceptionMessage::new
    );
}
