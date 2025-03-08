package com.cubicpulse.minestom.dhsupport.network.packets;

import net.minestom.server.network.NetworkBuffer;
import net.minestom.server.network.NetworkBufferTemplate;

public record DHFullDataPartialUpdateMessage(String levelKey, int bufferId, byte[] beacons) {
    
    public static final NetworkBuffer.Type<DHFullDataPartialUpdateMessage> SERIALIZER = NetworkBufferTemplate.template(
            NetworkBuffer.STRING, DHFullDataPartialUpdateMessage::levelKey,
            NetworkBuffer.INT, DHFullDataPartialUpdateMessage::bufferId,
            NetworkBuffer.RAW_BYTES, DHFullDataPartialUpdateMessage::beacons,
            DHFullDataPartialUpdateMessage::new
    );
    
}
