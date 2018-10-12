package net.runelite.client.plugins.freezetimer;

import com.google.common.eventbus.Subscribe;
import java.time.Instant;
import javax.inject.Inject;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.api.events.GraphicChanged;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(
        name = "Freeze Timer",
        description = "Displays an overlay indicating the amount of time a player will be frozen for",
        tags = {"freeze", "timer", "pk", "loudpacks"}
)
public class FreezeTimerPlugin extends Plugin {

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private FreezeTimerOverlay freezeTimerOverlay;

    @Inject
    private Client client;

    @Override
    protected void startUp() throws Exception {
        overlayManager.add(freezeTimerOverlay);
    }

    @Override
    protected void shutDown() throws Exception {
        overlayManager.remove(freezeTimerOverlay);
    }


    @Subscribe
    private void onGraphicsChange(GraphicChanged event) {
        if (client.getGameState().equals(GameState.LOGGED_IN)) {
            final Actor actor = event.getActor();
            if (actor instanceof Player && FreezeSpell.isValid(actor.getGraphic())) {
                freezeTimerOverlay.addInstance(new FreezeInstance(FreezeSpell.getSpellForId(actor.getGraphic()), (Player) actor, Instant.now()));
            }
        }
    }
}
