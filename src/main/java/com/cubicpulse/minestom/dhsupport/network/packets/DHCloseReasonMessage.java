package com.cubicpulse.minestom.dhsupport.network.packets;

import com.cubicpulse.minestom.dhsupport.network.NetworkTypes;
import net.minestom.server.network.NetworkBuffer;
import net.minestom.server.network.NetworkBufferTemplate;

public record DHCloseReasonMessage(String reason) {
    
    public static final NetworkBuffer.Type<DHCloseReasonMessage> SERIALIZER = NetworkBufferTemplate.template(
            NetworkTypes.INT_STRING, DHCloseReasonMessage::reason,
            DHCloseReasonMessage::new
    );
}
