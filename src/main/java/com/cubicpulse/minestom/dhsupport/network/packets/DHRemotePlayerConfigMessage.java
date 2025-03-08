package com.cubicpulse.minestom.dhsupport.network.packets;

import net.minestom.server.network.NetworkBuffer;
import net.minestom.server.network.NetworkBufferTemplate;

public record DHRemotePlayerConfigMessage(boolean distantGenerationEnabled, int renderDistance,
                                          int borderCenterX, int borderCenterZ, int borderRadius,
                                          int fullDataRequestConcurrencyLimit,
                                          boolean realTimeUpdatesEnabled, int realTimeUpdateRadius,
                                          boolean loginDataSyncEnabled, int loginDataSyncRadius, int loginDataSyncRcLimit,
                                          int maxDataTransferSpeed) {
    public static final NetworkBuffer.Type<DHRemotePlayerConfigMessage> SERIALIZER = NetworkBufferTemplate.template(
            NetworkBuffer.BOOLEAN, DHRemotePlayerConfigMessage::distantGenerationEnabled,
            NetworkBuffer.INT, DHRemotePlayerConfigMessage::renderDistance,
            NetworkBuffer.INT, DHRemotePlayerConfigMessage::borderCenterX,
            NetworkBuffer.INT, DHRemotePlayerConfigMessage::borderCenterZ,
            NetworkBuffer.INT, DHRemotePlayerConfigMessage::borderRadius,
            NetworkBuffer.INT, DHRemotePlayerConfigMessage::fullDataRequestConcurrencyLimit,
            NetworkBuffer.BOOLEAN, DHRemotePlayerConfigMessage::realTimeUpdatesEnabled,
            NetworkBuffer.INT, DHRemotePlayerConfigMessage::realTimeUpdateRadius,
            NetworkBuffer.BOOLEAN, DHRemotePlayerConfigMessage::loginDataSyncEnabled,
            NetworkBuffer.INT, DHRemotePlayerConfigMessage::loginDataSyncRadius,
            NetworkBuffer.INT, DHRemotePlayerConfigMessage::loginDataSyncRcLimit,
            NetworkBuffer.INT, DHRemotePlayerConfigMessage::maxDataTransferSpeed,
            DHRemotePlayerConfigMessage::new
    );
}
