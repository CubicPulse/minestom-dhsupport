package com.cubicpulse.minestom.dhsupport.events;

import net.minestom.server.entity.Player;

public class DHMessageInEvent extends DHMessageEvent {

    public DHMessageInEvent(Player player, Object message) {
        super(player, message);
    }
}
