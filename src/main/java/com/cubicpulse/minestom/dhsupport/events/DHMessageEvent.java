package com.cubicpulse.minestom.dhsupport.events;

import net.minestom.server.entity.Player;
import net.minestom.server.event.trait.CancellableEvent;
import net.minestom.server.event.trait.PlayerEvent;
import net.minestom.server.network.NetworkBuffer;
import org.jetbrains.annotations.NotNull;

public class DHMessageEvent implements CancellableEvent, PlayerEvent {
    
    private final Player player;
    private Object message;
    private boolean cancelled;
    
    public DHMessageEvent(Player player, Object message) {
        this.player = player;
        this.message = message;
    }
    
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }

    @Override
    public @NotNull Player getPlayer() {
        return player;
    }
    

    
    public Object getMessage() {
        return message;
    }
    
    public void setMessage(Object message) {
        this.message = message;
    }
    
}
