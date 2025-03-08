package com.cubicpulse.minestom.dhsupport.network.data;

import net.minestom.server.network.NetworkBuffer;
import net.minestom.server.network.NetworkBufferTemplate;

public class SectionPosition {

    public static final int DETAIL_LEVEL_WIDTH = 8;
    public static final int X_POS_WIDTH = 28;
    public static final int Z_POS_WIDTH = 28;

    public static final int DETAIL_LEVEL_OFFSET = 0;
    public static final int POS_X_OFFSET = DETAIL_LEVEL_OFFSET + DETAIL_LEVEL_WIDTH;
    public static final int POS_Z_OFFSET = POS_X_OFFSET + X_POS_WIDTH;

    public static final long DETAIL_LEVEL_MASK = Byte.MAX_VALUE;
    public static final int POS_X_MASK = (int) Math.pow(2, X_POS_WIDTH) - 1;
    public static final int POS_Z_MASK = (int) Math.pow(2, Z_POS_WIDTH) - 1;
    
    private int x;
    private int z;
    private int detailLevel;
    
    public static final NetworkBuffer.Type<SectionPosition> SERIALIZER = NetworkBufferTemplate.template(
            NetworkBuffer.LONG, SectionPosition::toLong,
            SectionPosition::new
    );
    
    public SectionPosition(long serialized) {
        fromLong(serialized);
    }
    
    public SectionPosition(int x, int z, int detailLevel) {
        this.x = x;
        this.z = z;
        this.detailLevel = detailLevel;
    }

    public void setDetailLevel(int level)
    {
        this.detailLevel = level;
    }

    public int getDetailLevel()
    {
        return this.detailLevel;
    }

    public void setX(int x)
    {
        this.x = x;
    }

    public int getX()
    {
        return this.x;
    }

    public void setZ(int z)
    {
        this.z = z;
    }

    public int getZ()
    {
        return this.z;
    }
    
    public long toLong()
    {
        long data = 0;

        data |= this.detailLevel & DETAIL_LEVEL_MASK;
        data |= (long) (this.x & POS_X_MASK) << POS_X_OFFSET;
        data |= (long) (this.z & POS_Z_MASK) << POS_Z_OFFSET;

        return data;
    }

    public void fromLong(long data)
    {
        this.detailLevel = (int) (data & DETAIL_LEVEL_MASK);
        this.x = (int) ((data >> POS_X_OFFSET) & POS_X_MASK);
        this.z = (int) ((data >> POS_Z_OFFSET) & POS_Z_MASK);

        // Adjust for potential negative values if masks do not account for sign
        if ((this.x & (1 << 23)) != 0) { // Check if the sign bit is set for 24-bit value
            this.x |= ~POS_X_MASK; // Sign extend
        }
        if ((this.z & (1 << 23)) != 0) { // Check if the sign bit is set for 24-bit value
            this.z |= ~POS_Z_MASK; // Sign extend
        }
    }
    
}
