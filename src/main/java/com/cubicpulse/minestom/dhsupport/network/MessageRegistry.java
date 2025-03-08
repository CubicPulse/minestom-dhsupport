package com.cubicpulse.minestom.dhsupport.network;

import net.minestom.server.network.NetworkBuffer;

import java.util.HashMap;
import java.util.Map;

public class MessageRegistry {
    
    private final Map<Integer, MessageType> types = new HashMap<>();
    private record MessageType(NetworkBuffer.Type<?> type, Class<?> clazz) {}
    
    public void register(int id, Class<?> clazz) {
        try {
            var serializer = clazz.getDeclaredField("SERIALIZER");
            var type = (NetworkBuffer.Type<?>) serializer.get(null);
            types.put(id, new MessageType(type, clazz));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException("No serializer found for class: " + clazz);
        }
    }
    
    public <T> void register(int id, NetworkBuffer.Type<T> type, Class<T> clazz) {
        types.put(id, new MessageType(type, clazz));
    }
    
    public NetworkBuffer.Type<?> getType(int id) {
        MessageType type = types.get(id);
        if (type == null) {
            throw new IllegalArgumentException("Unknown message type: " + id);
        }
        return type.type;
    }
    
    public Class<?> getClass(int id) {
        MessageType type = types.get(id);
        if (type == null) {
            throw new IllegalArgumentException("Unknown message type: " + id);
        }
        return type.clazz;
    }
    
    public int getId(Class<?> clazz) {
        for (Map.Entry<Integer, MessageType> entry : types.entrySet()) {
            if (entry.getValue().clazz.equals(clazz)) {
                return entry.getKey();
            }
        }
        throw new IllegalArgumentException("Unknown message class: " + clazz);
    }
    
    
    
}
