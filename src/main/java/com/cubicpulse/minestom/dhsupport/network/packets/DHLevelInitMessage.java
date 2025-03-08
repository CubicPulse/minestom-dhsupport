package com.cubicpulse.minestom.dhsupport.network.packets;

import com.cubicpulse.minestom.dhsupport.network.NetworkTypes;
import net.minestom.server.network.NetworkBuffer;
import net.minestom.server.network.NetworkBufferTemplate;

public record DHLevelInitMessage(String key, long time) {
    
    public static final NetworkBuffer.Type<DHLevelInitMessage> SERIALIZER = NetworkBufferTemplate.template(
            NetworkTypes.SHORT_STRING, DHLevelInitMessage::key,
            NetworkBuffer.LONG, DHLevelInitMessage::time,
            DHLevelInitMessage::new
    );
}
