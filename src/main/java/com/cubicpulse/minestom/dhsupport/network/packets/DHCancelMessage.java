package com.cubicpulse.minestom.dhsupport.network.packets;

import net.minestom.server.network.NetworkBuffer;
import net.minestom.server.network.NetworkBufferTemplate;

public record DHCancelMessage(int tracker) {
    
    public static final NetworkBuffer.Type<DHCancelMessage> SERIALIZER = NetworkBufferTemplate.template(
            NetworkBuffer.INT, DHCancelMessage::tracker,
            DHCancelMessage::new
    );
    
}
