package com.cubicpulse.minestom.dhsupport;

import com.cubicpulse.minestom.dhsupport.db.DistantHorizonsDb;
import com.cubicpulse.minestom.dhsupport.network.PluginMessageHandler;
import com.cubicpulse.minestom.dhsupport.network.packets.DHRemotePlayerConfigMessage;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.tag.Tag;

import java.util.UUID;

public class DHMinestom {
    
    public static final Tag<UUID> DH_INSTANCE_UUID = Tag.UUID("dh_instance_uuid");
    public static final Tag<DistantHorizonsDb> DH_DB = Tag.Transient("dh_db");
    public static final Tag<DHWorldSettings> DH_WORLD_SETTINGS = Tag.Transient("dh_world_settings");
    public static final Tag<DHRemotePlayerConfigMessage> DH_REMOTE_PLAYER_CONFIG = Tag.Transient("dh_remote_player_config");
    
    public static void init() {
        // Initialize the plugin
        var pmh = new PluginMessageHandler();
        var eventNode = EventNode.all("DHMinestom");
        eventNode.addListener(PlayerSpawnEvent.class, event -> {
            // If the player is switching worlds and has a config, update the LevelInit for the player
            if (!event.isFirstSpawn() && event.getPlayer().hasTag(DH_REMOTE_PLAYER_CONFIG)) {
                var config = event.getPlayer().getTag(DH_REMOTE_PLAYER_CONFIG);
                pmh.handleConfig(event.getPlayer(), config);
            }
        });
        MinecraftServer.getGlobalEventHandler().addChild(eventNode);
    }
    
    public static void registerInstance(Instance instance, DistantHorizonsDb db, UUID uuidInDbFile) {
        instance.setTag(DH_INSTANCE_UUID, uuidInDbFile);
        instance.setTag(DH_DB, db);
    }
    
    public static void setDhWorldSettings(Instance instance, DHWorldSettings settings) {
        instance.setTag(DH_WORLD_SETTINGS, settings);
    }
    
}
