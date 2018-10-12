package net.runelite.client.plugins.damagetracker;

import com.google.common.eventbus.Subscribe;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import javax.inject.Inject;
import javax.inject.Provider;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.SpriteID;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.api.events.InteractingChanged;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ImageUtil;


@PluginDescriptor(
        name = "Damage Tracker",
        description = "Tracks damage done to other players and NPCs.",
        tags = {"damage", "tracker", "pk", "loudpacks"},
        enabledByDefault = true
)
@Slf4j
public class DamageTrackerPlugin extends Plugin {

    private static final Duration WAIT = Duration.ofSeconds(5);
    @Getter
    private final HashSet<DamageTrackerItemEntry> entries = new HashSet<>();
    @Inject
    private ClientToolbar clientToolbar;
    @Inject
    private Provider<SpriteManager> spriteManager;
    @Inject
    private Client client;
    @Inject
    private Provider<OverlayManager> overlayManager;
    private DamageTrackerPanel panel;
    private NavigationButton navButton;
    @Getter
    private Actor lastOpponent;
    private Instant lastTime;
    @Getter
    private HashMap<Actor, DamageTrackerItemEntry> targetMap = new HashMap<>();

    @Override
    protected void startUp() throws Exception {
        panel = new DamageTrackerPanel(this);
        spriteManager.get().getSpriteAsync(SpriteID.HITSPLAT_RED_DAMAGE, 0, panel::loadHeaderIcon);

        final BufferedImage icon = ImageUtil.getResourceStreamFromClass(getClass(), "icon.png");
        navButton = NavigationButton.builder()
                .tooltip("Damage Tracker")
                .icon(icon)
                .priority(8)
                .panel(panel)
                .build();

        clientToolbar.addNavigation(navButton);
    }

    @Override
    protected void shutDown() {
        clientToolbar.removeNavigation(navButton);
    }

    @Subscribe
    private void onHitSplatApplied(HitsplatApplied event) {
        if (lastOpponent != null && event.getActor().equals(lastOpponent)) {
            targetMap.put(lastOpponent, new DamageTrackerItemEntry(lastOpponent.getName(), lastOpponent.getCombatLevel()));
            panel.addTotalDamage(event.getHitsplat().getAmount());
            panel.updateEntries(targetMap);
            DamageTrackerBox entryBox = panel.getEntryBox(lastOpponent);
            if (entryBox != null) {
                DamageTrackerItemEntry entry = entryBox.getEntry();
                entry.addDamage(event.getHitsplat().getAmount());
                entryBox.updateEntry();
            }
        }
    }


    @Subscribe
    public void onInteractingChanged(InteractingChanged event) {
        if (event.getSource() != client.getLocalPlayer()) {
            return;
        }

        Actor opponent = event.getTarget();

        if (opponent == null) {
            lastTime = Instant.now();
            return;
        }

        lastOpponent = (opponent instanceof Player && !opponent.equals(client.getLocalPlayer())) ? opponent : null;
    }

    @Subscribe
    public void onGameTick(GameTick gameTick) {
        if (lastOpponent != null && client.getLocalPlayer().getInteracting() == null) {
            if (Duration.between(lastTime, Instant.now()).compareTo(WAIT) > 0) {
                lastOpponent = null;
            }
        }
    }
}

