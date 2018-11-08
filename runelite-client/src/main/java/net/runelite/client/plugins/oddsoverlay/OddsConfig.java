package net.runelite.client.plugins.oddsoverlay;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("oddsoverlay")
public interface OddsConfig extends Config
{
	@ConfigItem(
		position = 0,
		keyName = "autoAccept",
		name = "Auto Accept",
		description = "Auto accept duels"
	)
	default boolean autoAccept()
	{
		return false;
	}

	@ConfigItem(
		position = 1,
		keyName = "oddsThreshold",
		name = "Odds Threshold",
		description = "Auto accept with odds equal to or greater than this value (%)"
	)
	default int oddsThreshold()
	{
		return 55;
	}
}
