package net.runelite.client.plugins.prayerflick;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("prayerflick")
public interface PrayerFlickConfig extends Config {

    @ConfigItem(
            keyName = "running",
            name = "Server Enabled",
            description = "Starts / stops the server",
            position = 0
    )
    default boolean serverEnabled()
    {
        return false;
    }

    @ConfigItem(
            position = 1,
            keyName = "enabled",
            name = "Pray flick enabled",
            description = "One tick flicks quick prayers"
    )
    default boolean enabled() {
        return false;
    }

}
