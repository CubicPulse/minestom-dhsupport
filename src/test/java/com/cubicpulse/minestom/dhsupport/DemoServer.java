package com.cubicpulse.minestom.dhsupport;

import com.cubicpulse.minestom.dhsupport.db.DistantHorizonsDb;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.extras.MojangAuth;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.anvil.AnvilLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.sql.SQLException;
import java.util.UUID;

public class DemoServer {

    private static final Logger log = LoggerFactory.getLogger(DemoServer.class);

    public static void main(String[] args) {
        MinecraftServer minecraftServer = MinecraftServer.init();
        
        var instanceManager = minecraftServer.getInstanceManager();
        var container = instanceManager.createInstanceContainer();
        container.setChunkLoader(new AnvilLoader("world"));
        container.setChunkSupplier(LightingChunk::new);
        
        DistantHorizonsDb dhDb;
        try {
            dhDb = new DistantHorizonsDb(Path.of("world", "data.sqlite"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        
        DHMinestom.init();
        DHMinestom.registerInstance(container, dhDb, UUID.fromString("69d8bccd-d133-4637-a0b3-57bc4753fb93"));
        DHMinestom.setDhWorldSettings(container, new DHWorldSettings(4096,2560, 2560, 2560));

        MinecraftServer.getGlobalEventHandler().addListener(AsyncPlayerConfigurationEvent.class, 
                event -> event.setSpawningInstance(container));
        
        MinecraftServer.getGlobalEventHandler().addListener(PlayerSpawnEvent.class, event -> {
            event.getPlayer().teleport(new Pos(849, 68, 2120));
            event.getPlayer().setGameMode(GameMode.SPECTATOR);
        });
        MinecraftServer.getExceptionManager().setExceptionHandler(e -> log.error("Unhandled exception", e));

        MojangAuth.init();
        minecraftServer.start("0.0.0.0", 25565);
    }
    
}
