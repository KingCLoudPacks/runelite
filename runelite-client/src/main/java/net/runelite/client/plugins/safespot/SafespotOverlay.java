package net.runelite.client.plugins.safespot;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.OverlayUtil;


public class SafespotOverlay extends Overlay {

    private SafespotPlugin plugin;

    private SafespotConfig config;

    private Client client;

    @Inject
    private SafespotOverlay(SafespotPlugin plugin, SafespotConfig config, Client client) {
        this.plugin = plugin;
        this.config = config;
        this.client = client;

        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.LOW);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics) {

        for (WorldPoint point : plugin.getTileList()) {
            if (point.getPlane() != client.getPlane()) {
                continue;
            }

            drawTile(graphics, point);
        }


        return null;
    }

    private void drawTile(Graphics2D graphics, WorldPoint point) {
        WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();

        if (point.distanceTo(playerLocation) >= 32) {
            return;
        }

        LocalPoint lp = LocalPoint.fromWorld(client, point);
        if (lp == null) {
            return;
        }

        Polygon poly = Perspective.getCanvasTilePoly(client, lp);
        if (poly == null) {
            return;
        }

        OverlayUtil.renderPolygon(graphics, poly, Color.RED);
    }


}
