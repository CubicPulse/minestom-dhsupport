package com.cubicpulse.minestom.dhsupport.db;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.UUID;

public class DistantHorizonsDb {
    
    private final Logger log;
    
    private final Connection connection;
    
    public DistantHorizonsDb(Path dbPath) throws SQLException {
        this.connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
        log = LoggerFactory.getLogger(DistantHorizonsDb.class.getName() + "." + dbPath.getFileName());
        if (connection.isClosed()) {
            throw new SQLException("Failed to connect to database");
        }
    }
    
    public void close() throws SQLException {
        if (connection == null || connection.isClosed()) {
            return;
        }
        
        connection.close();
    }
    
    public @Nullable DHLod getLod(UUID worldId, int sectionX, int sectionZ) {
        var sql = "SELECT data, beacons, timestamp FROM lods WHERE worldId = ? AND x = ? AND z = ?";
        
        try (var statement = connection.prepareStatement(sql)) {
            statement.setString(1, worldId.toString());
            statement.setInt(2, sectionX);
            statement.setInt(3, sectionZ);
            
            var result = statement.executeQuery();
            if (result.next()) {
                var data = result.getBytes("data");
                if (data == null) {
                    return null;
                }
                
                return new DHLod(worldId, sectionX, sectionZ, 
                        data, result.getBytes("beacons"), result.getInt("timestamp"));
            }
        } catch (SQLException e) {
            log.error("Failed to get lod for world {} at {}, {}", worldId, sectionX, sectionZ, e);
        }
        return null;
    }
    
    public boolean lodExists(UUID worldId, int sectionX, int sectionZ) {
        var sql = "SELECT EXISTS(SELECT 1 FROM lods WHERE worldId = ? AND x = ? AND z = ?)";
        
        try (var statement = connection.prepareStatement(sql)) {
            statement.setString(1, worldId.toString());
            statement.setInt(2, sectionX);
            statement.setInt(3, sectionZ);
            
            var result = statement.executeQuery();
            return result.getInt(1) > 0;
        } catch (SQLException e) {
            log.error("Failed to check if lod exists for world {} at {}, {}", worldId, sectionX, sectionZ, e);
        }
        return false;
    }
    
}
