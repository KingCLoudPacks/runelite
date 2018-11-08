package net.runelite.client.plugins.potionsipper;

import com.loudpacks.script.combat.CombatScript;
import javax.inject.Inject;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@PluginDescriptor(
	name = "Potion Sipper",
	description = "Sips potions and eats automatically",
	tags = {"potion", "sipper", "eat", "loudpacks"}
)
public class PotionSipperPlugin extends Plugin
{

	@Inject
	private CombatScript combatScript;

	@Override
	protected void startUp() throws Exception
	{
		combatScript.setRunning(true);
	}

	@Override
	protected void shutDown() throws Exception
	{
		combatScript.setRunning(false);
	}
}
