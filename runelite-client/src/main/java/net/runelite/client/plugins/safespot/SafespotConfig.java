package net.runelite.client.plugins.safespot;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("safespot")
public interface SafespotConfig extends Config {
    @ConfigItem(
            keyName = "tileRadius",
            name = "Tile Radius",
            description = "Max distance from target to calculate safespots",
            position = 1
    )
    default int tileRadius() {
        return 10;
    }

    @ConfigItem(
            keyName = "targetTimeout",
            name = "Target Timeout",
            description = "Amount of time in seconds to clear your target when not interacting",
            position = 2
    )
    default int targetTimeout() {
        return 10;
    }

}
