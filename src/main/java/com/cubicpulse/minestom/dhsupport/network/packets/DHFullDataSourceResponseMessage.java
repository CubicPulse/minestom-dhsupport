package com.cubicpulse.minestom.dhsupport.network.packets;

import net.minestom.server.network.NetworkBuffer;
import org.jetbrains.annotations.NotNull;

public record DHFullDataSourceResponseMessage(int tracker, Integer bufferId, byte[] beacons) {
    
    public DHFullDataSourceResponseMessage(int tracker) {
        this(tracker, null, null);
    }
    
    public static final NetworkBuffer.Type<DHFullDataSourceResponseMessage> SERIALIZER = new NetworkBuffer.Type<DHFullDataSourceResponseMessage>() {
        @Override
        public void write(@NotNull NetworkBuffer networkBuffer, DHFullDataSourceResponseMessage model) {
            networkBuffer.write(NetworkBuffer.INT, model.tracker());
            if (model.bufferId() != null) {
                networkBuffer.write(NetworkBuffer.BOOLEAN, true);
                networkBuffer.write(NetworkBuffer.INT, model.bufferId());
                networkBuffer.write(NetworkBuffer.RAW_BYTES, model.beacons());
            } else {
                networkBuffer.write(NetworkBuffer.BOOLEAN, false);
            }
        }

        @Override
        public DHFullDataSourceResponseMessage read(@NotNull NetworkBuffer networkBuffer) {
            throw new UnsupportedOperationException("Not implemented yet");
        }
    };
    
}
