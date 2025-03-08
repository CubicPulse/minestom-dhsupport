package com.cubicpulse.minestom.dhsupport.db;

import java.util.UUID;

public record DHLod(UUID worldId, int x, int z, byte[] data, byte[] beacons, int timestamp) {
}
