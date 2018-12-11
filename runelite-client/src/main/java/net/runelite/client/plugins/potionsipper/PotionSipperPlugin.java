package net.runelite.client.plugins.potionsipper;

import com.loudpacks.script.combat.CombatScript;
import javax.inject.Inject;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(
	name = "Potion Sipper",
	description = "Sips potions and eats automatically",
	tags = {"potion", "sipper", "eat", "loudpacks"}
)
public class PotionSipperPlugin extends Plugin
{

	@Inject
	private CombatScript combatScript;

	@Inject
	private PotionSipperOverlay overlay;

	@Inject
	private OverlayManager overlayManager;

	@Override
	protected void startUp() throws Exception
	{
		combatScript.setRunning(true);
		overlayManager.add(overlay);

	}

	@Override
	protected void shutDown() throws Exception
	{
		combatScript.setRunning(false);
		overlayManager.remove(overlay);
	}
}
