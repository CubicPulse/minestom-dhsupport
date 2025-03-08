package com.cubicpulse.minestom.dhsupport.network.packets;

import com.cubicpulse.minestom.dhsupport.network.NetworkTypes;
import com.cubicpulse.minestom.dhsupport.network.data.SectionPosition;
import net.minestom.server.network.NetworkBuffer;
import net.minestom.server.network.NetworkBufferTemplate;

public record DHFullDataSourceRequestMessage(int tracker, String worldName, SectionPosition sectionPosition, Long timestamp) {
    
    public static final NetworkBuffer.Type<DHFullDataSourceRequestMessage> SERIALIZER = NetworkBufferTemplate.template(
            NetworkBuffer.INT, DHFullDataSourceRequestMessage::tracker,
            NetworkTypes.SHORT_STRING, DHFullDataSourceRequestMessage::worldName,
            SectionPosition.SERIALIZER, DHFullDataSourceRequestMessage::sectionPosition,
            NetworkBuffer.LONG.optional(), DHFullDataSourceRequestMessage::timestamp,
            DHFullDataSourceRequestMessage::new
    );
    
}
