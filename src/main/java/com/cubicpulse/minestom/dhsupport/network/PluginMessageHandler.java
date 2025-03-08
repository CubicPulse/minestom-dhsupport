package com.cubicpulse.minestom.dhsupport.network;

import com.cubicpulse.minestom.dhsupport.DHMinestom;
import com.cubicpulse.minestom.dhsupport.DHWorldSettings;
import com.cubicpulse.minestom.dhsupport.db.DistantHorizonsDb;
import com.cubicpulse.minestom.dhsupport.events.DHMessageInEvent;
import com.cubicpulse.minestom.dhsupport.events.DHMessageOutEvent;
import com.cubicpulse.minestom.dhsupport.network.packets.*;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventDispatcher;
import net.minestom.server.event.player.PlayerPluginMessageEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.network.NetworkBuffer;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PluginMessageHandler {

    private static final Logger log = LoggerFactory.getLogger(PluginMessageHandler.class);
    public static int CHUNK_SIZE = 1024 * 16; // TODO: Configurable?
    
    private final MessageRegistry messageRegistry;

    public final String pluginChannel = "distant_horizons:message";

    public final int protocolVersion = 10;
    
    public PluginMessageHandler() {
        this.messageRegistry = new MessageRegistry();
        this.messageRegistry.register(1, DHCloseReasonMessage.class);
        this.messageRegistry.register(2, DHLevelInitMessage.class);
        this.messageRegistry.register(3, DHRemotePlayerConfigMessage.class);
        this.messageRegistry.register(4, DHCancelMessage.class);
        this.messageRegistry.register(5, DHExceptionMessage.class);
        this.messageRegistry.register(6, DHFullDataSourceRequestMessage.class);
        this.messageRegistry.register(7, DHFullDataSourceResponseMessage.class);
        this.messageRegistry.register(8, DHFullDataPartialUpdateMessage.class);
        this.messageRegistry.register(9, DHFullDataChunkMessage.class);

        MinecraftServer.getGlobalEventHandler().addListener(PlayerPluginMessageEvent.class, this::onPluginMessage);
    }

    private void onPluginMessage(@NotNull PlayerPluginMessageEvent playerPluginMessageEvent) {
        if (!playerPluginMessageEvent.getIdentifier().equals(pluginChannel)) {
            return;
        }
        
        var player = playerPluginMessageEvent.getPlayer();

        var buffer = NetworkBuffer.wrap(playerPluginMessageEvent.getMessage(), 0, playerPluginMessageEvent.getMessage().length);
        short protocolVersion = buffer.read(NetworkBuffer.SHORT);
        if (protocolVersion != this.protocolVersion) {
            var closeMessage = new DHCloseReasonMessage(
                    (protocolVersion > this.protocolVersion ? "Server" : "Client") + " is outdated."
            );
            sendPluginMessage(player, DHCloseReasonMessage.SERIALIZER, closeMessage);
            return;
        }
        
        short messageType = buffer.read(NetworkBuffer.SHORT);
        var type = messageRegistry.getType(messageType);
        if (type == null) {
            var closeMessage = new DHCloseReasonMessage("Unknown message type: " + messageType);
            sendPluginMessage(player, DHCloseReasonMessage.SERIALIZER, closeMessage);
            return;
        }
        
        var message = type.read(buffer);
        var event = new DHMessageInEvent(player, message);
        EventDispatcher.callCancellable(event, () -> handleMessage(player, event.getMessage()));
    }

    private void handleMessage(Player player, Object message) {
        switch (message) {
            case DHFullDataSourceRequestMessage request -> handleRequest(player, request);
            case DHRemotePlayerConfigMessage config -> handleConfig(player, config);
            default -> log.warn("Unhandled DHSupport message: {}", message);
        }
    }

    private static UUID getInstanceUuid(Instance instance) {
        if (instance.hasTag(DHMinestom.DH_INSTANCE_UUID)) {
            return instance.getTag(DHMinestom.DH_INSTANCE_UUID);
        }
        return instance.getUuid();
    }
    
    public void handleConfig(Player player, @Nullable DHRemotePlayerConfigMessage config) {
        if (config == null) config = player.getTag(DHMinestom.DH_REMOTE_PLAYER_CONFIG);
        if (config == null) {
            log.error("No config found for player {}", player.getUsername());
            return;
        }
        
        var instance = player.getInstance();
        var instanceId = getInstanceUuid(instance);
        DHLevelInitMessage levelInitMessage = new DHLevelInitMessage(
                instanceId.toString(),
                System.currentTimeMillis()
        );
        log.debug("Sending level init message to player {}: {}", player.getUsername(), levelInitMessage);
        player.setTag(DHMinestom.DH_REMOTE_PLAYER_CONFIG, config);
        sendPluginMessage(player, DHLevelInitMessage.SERIALIZER, levelInitMessage);
        
        var db = instance.getTag(DHMinestom.DH_DB);
        
        var worldConfig = instance.getTag(DHMinestom.DH_WORLD_SETTINGS);
        if (worldConfig == null) {
            log.warn("No world config found for instance {}", instance);
            sendPluginMessage(player, DHRemotePlayerConfigMessage.SERIALIZER, new DHRemotePlayerConfigMessage(
                    db != null,
                    1024,
                    0, 0, 0, 20, false, 0, false, 0, 0, 500
            ));
        } else {
            sendPluginMessage(player, DHRemotePlayerConfigMessage.SERIALIZER, new DHRemotePlayerConfigMessage(
                    db != null,
                    Math.min(config.renderDistance(), worldConfig.maxRenderDistance()),
                    worldConfig.borderCenterX(), worldConfig.borderCenterZ(), worldConfig.borderRadius(), 
                    20, false, 0, false, 0, 0, 500
            ));
        }
    }

    private final Tag<Integer> DH_BUFFER_ID = Tag.Integer("dh_buffer_id");
    public static int THREAD_POOL_SIZE = Integer.parseInt(System.getProperty("minestom.dh.thread_pool_size", "4"));
    private final ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    private void handleRequest(Player player, DHFullDataSourceRequestMessage request) {
        // Only support detail level 6 for now
        if (request.sectionPosition().getDetailLevel() != 6) {
            var exception = new DHExceptionMessage(request.tracker(), DHExceptionMessage.TYPE_SECTION_REQUIRES_SPLITTING, "Unsupported detail level: " + request.sectionPosition().getDetailLevel());
            sendPluginMessage(player, DHExceptionMessage.SERIALIZER, exception);
            return;
        }
        
        if (!player.getInstance().hasTag(DHMinestom.DH_DB)) {
            var exception = new DHExceptionMessage(request.tracker(), DHExceptionMessage.TYPE_REQUEST_REJECTED, "No database attached to instance");
            sendPluginMessage(player, DHExceptionMessage.SERIALIZER, exception);
            return;
        }
        
        UUID worldUuid = getInstanceUuid(player.getInstance());
        DistantHorizonsDb db = player.getInstance().getTag(DHMinestom.DH_DB);
        var worldConfig = player.getInstance().getTag(DHMinestom.DH_WORLD_SETTINGS);
        
        if (worldConfig != null && handleWorldConfigPolicy(player, request, worldConfig)) {
            return;
        }
        
        var pos = request.sectionPosition();
        
        executor.submit(() -> {
            if (!db.lodExists(worldUuid, pos.getX(), pos.getZ())) {
                var exception = new DHExceptionMessage(request.tracker(), DHExceptionMessage.TYPE_REQUEST_OUT_OF_RANGE, "Section not found");
                sendPluginMessage(player, DHExceptionMessage.SERIALIZER, exception);
                return;
            }
            
            var lod = db.getLod(worldUuid, pos.getX(), pos.getZ());
            if (lod == null) {
                var exception = new DHExceptionMessage(request.tracker(), DHExceptionMessage.TYPE_REQUEST_OUT_OF_RANGE, "Section not found");
                sendPluginMessage(player, DHExceptionMessage.SERIALIZER, exception);
                return;
            }

            boolean sendData = request.timestamp() == null || (request.timestamp() / 1000) < lod.timestamp();
            sendData &= lod.data().length > 0;
            if (!sendData) {
                var response = new DHFullDataSourceResponseMessage(request.tracker());
                sendPluginMessage(player, DHFullDataSourceResponseMessage.SERIALIZER, response);
                return;
            }
            
            byte[] data = lod.data();
            int myBufferId;
            if (!player.hasTag(DH_BUFFER_ID)) {
                myBufferId = 1;
                player.setTag(DH_BUFFER_ID, myBufferId);
            } else {
                myBufferId = player.getAndUpdateTag(DH_BUFFER_ID, a -> a + 1);
            }

            int chunkCount = sendChunkBufferToPlayer(player, data, myBufferId);
            if (chunkCount == 0) {
                var response = new DHFullDataSourceResponseMessage(request.tracker());
                sendPluginMessage(player, DHFullDataSourceResponseMessage.SERIALIZER, response);
                return;
            }

            DHFullDataSourceResponseMessage response = new DHFullDataSourceResponseMessage(
                    request.tracker(),
                    myBufferId,
                    lod.beacons()
            );
            sendPluginMessage(player, DHFullDataSourceResponseMessage.SERIALIZER, response);
        });
        
    }

    protected static final int BLOCK_TO_CHUNK_POWER = 4;
    protected static final int CHUNK_TO_SECTION_POWER = 2;
    private boolean handleWorldConfigPolicy(Player player, DHFullDataSourceRequestMessage request, DHWorldSettings worldConfig) {
        var pos = request.sectionPosition();
        var worldX = pos.getX() << (BLOCK_TO_CHUNK_POWER + CHUNK_TO_SECTION_POWER);
        var worldZ = pos.getZ() << (BLOCK_TO_CHUNK_POWER + CHUNK_TO_SECTION_POWER);
        var playerX = player.getPosition().blockX();
        var playerZ = player.getPosition().blockZ();
        
        if (worldConfig.borderRadius() > 0) {
            int minX = worldConfig.borderCenterX() - worldConfig.borderRadius();
            int maxX = worldConfig.borderCenterX() + worldConfig.borderRadius();
            int minZ = worldConfig.borderCenterZ() - worldConfig.borderRadius();
            int maxZ = worldConfig.borderCenterZ() + worldConfig.borderRadius();
            int higherLodX = worldX + 64;
            int higherLodZ = worldZ + 64;
            
            if (higherLodX < minX || worldX > maxX || higherLodZ < minZ || worldZ > maxZ) {
                var exception = new DHExceptionMessage(request.tracker(), DHExceptionMessage.TYPE_REQUEST_REJECTED, "Section out of world border");
                sendPluginMessage(player, DHExceptionMessage.SERIALIZER, exception);
                return true;
            }
        }
        
        if (worldConfig.maxRenderDistance() > 0) {
            int distance = fastDistance(playerX, playerZ, worldX, worldZ);
            if (distance > worldConfig.maxRenderDistance() * worldConfig.maxRenderDistance()) {
                var exception = new DHExceptionMessage(request.tracker(), DHExceptionMessage.TYPE_REQUEST_OUT_OF_RANGE, "Section out of render distance");
                sendPluginMessage(player, DHExceptionMessage.SERIALIZER, exception);
                return true;
            }
        }
        
        return false;
    }
    
    private static int fastDistance(int x1, int z1, int x2, int z2) {
        int dx = x1 - x2;
        int dz = z1 - z2;
        return dx * dx + dz * dz;
    }

    private int sendChunkBufferToPlayer(Player player, byte[] data, int myBufferId) {
        int chunkCount = (int) Math.ceil((double) data.length / CHUNK_SIZE);

        for (int chunkNo = 0; chunkNo < chunkCount; chunkNo++) {
            DHFullDataChunkMessage chunkResponse = new DHFullDataChunkMessage(
                    myBufferId,
                    Arrays.copyOfRange(
                            data,
                            CHUNK_SIZE * chunkNo,
                            Math.min(CHUNK_SIZE * chunkNo + CHUNK_SIZE, data.length)
                    ),
                    chunkNo == 0
            );
            sendPluginMessage(player, DHFullDataChunkMessage.SERIALIZER, chunkResponse);
        }
        
        return chunkCount;
    }

    public <T> void sendPluginMessage(Player player, NetworkBuffer.Type<T> serializer, T obj) {
        var event = new DHMessageOutEvent(player, serializer, obj);
        EventDispatcher.callCancellable(event, () -> sendPluginMessageFromEvent(player, event));
    }

    private void sendPluginMessageFromEvent(Player player, DHMessageOutEvent event) {
        var bytes = NetworkBuffer.makeArray(buffer -> {
            buffer.write(NetworkBuffer.SHORT, (short) protocolVersion);
            var obj = event.getMessage();
            buffer.write(NetworkBuffer.SHORT, (short) messageRegistry.getId(obj.getClass()));
            var serializer = event.getSerializer();
            serializer.write(buffer, obj);
        });
        player.sendPluginMessage(pluginChannel, bytes);
    }


}
