package com.cubicpulse.minestom.dhsupport.network;

import net.minestom.server.network.NetworkBuffer;
import org.jetbrains.annotations.NotNull;

public class NetworkTypes {
    
    public static final NetworkBuffer.Type<String> SHORT_STRING = new NetworkBuffer.Type<String>() {
        @Override
        public void write(@NotNull NetworkBuffer networkBuffer, String s) {
            networkBuffer.write(NetworkBuffer.SHORT, (short) s.length());
            networkBuffer.write(NetworkBuffer.RAW_BYTES, s.getBytes());
        }

        @Override
        public String read(@NotNull NetworkBuffer networkBuffer) {
            var length = networkBuffer.read(NetworkBuffer.SHORT);
            var bytes = networkBuffer.read(NetworkBuffer.FixedRawBytes(length));
            return new String(bytes);
        }
    };

    public static final NetworkBuffer.Type<String> INT_STRING = new NetworkBuffer.Type<String>() {
        @Override
        public void write(@NotNull NetworkBuffer networkBuffer, String s) {
            networkBuffer.write(NetworkBuffer.INT, s.length());
            networkBuffer.write(NetworkBuffer.RAW_BYTES, s.getBytes());
        }

        @Override
        public String read(@NotNull NetworkBuffer networkBuffer) {
            var length = networkBuffer.read(NetworkBuffer.INT);
            var bytes = networkBuffer.read(NetworkBuffer.FixedRawBytes(length));
            return new String(bytes);
        }
    };
    
    public static final NetworkBuffer.Type<byte[]> INT_PREFIXED_BYTE_ARRAY = new NetworkBuffer.Type<byte[]>() {
        @Override
        public void write(@NotNull NetworkBuffer networkBuffer, byte[] bytes) {
            networkBuffer.write(NetworkBuffer.INT, bytes.length);
            networkBuffer.write(NetworkBuffer.RAW_BYTES, bytes);
        }

        @Override
        public byte[] read(@NotNull NetworkBuffer networkBuffer) {
            var length = networkBuffer.read(NetworkBuffer.INT);
            return networkBuffer.read(NetworkBuffer.FixedRawBytes(length));
        }
    };
}
