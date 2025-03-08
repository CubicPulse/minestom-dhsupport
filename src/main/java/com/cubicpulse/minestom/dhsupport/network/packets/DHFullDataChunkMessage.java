package com.cubicpulse.minestom.dhsupport.network.packets;

import com.cubicpulse.minestom.dhsupport.network.NetworkTypes;
import net.minestom.server.network.NetworkBuffer;
import net.minestom.server.network.NetworkBufferTemplate;

public record DHFullDataChunkMessage(int bufferId, byte[] data, boolean isFirst) {
    
    public static final NetworkBuffer.Type<DHFullDataChunkMessage> SERIALIZER = NetworkBufferTemplate.template(
            NetworkBuffer.INT, DHFullDataChunkMessage::bufferId,
            NetworkTypes.INT_PREFIXED_BYTE_ARRAY, DHFullDataChunkMessage::data,
            NetworkBuffer.BOOLEAN, DHFullDataChunkMessage::isFirst,
            DHFullDataChunkMessage::new
    );
}
