package net.runelite.client.plugins.safespot;

import lombok.Value;

@Value
public class SafeTilePoint {
    private int regionId;
    private int regionX;
    private int regionY;
    private int z;
}