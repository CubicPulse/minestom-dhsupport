package com.cubicpulse.minestom.dhsupport.events;

import net.minestom.server.entity.Player;
import net.minestom.server.network.NetworkBuffer;

public class DHMessageOutEvent extends DHMessageEvent {
    
    private NetworkBuffer.Type<Object> serializer;
    
    @SuppressWarnings("unchecked")
    public DHMessageOutEvent(Player player, NetworkBuffer.Type<?> serializer, Object message) {
        super(player, message);
        this.serializer = (NetworkBuffer.Type<Object>) serializer;
    }

    public NetworkBuffer.Type<Object> getSerializer() {
        return serializer;
    }

    @SuppressWarnings("unchecked")
    public void setSerializer(NetworkBuffer.Type<?> serializer) {
        this.serializer = (NetworkBuffer.Type<Object>) serializer;
    }
    
}
