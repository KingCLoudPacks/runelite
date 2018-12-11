package net.runelite.client.plugins.potionsipper;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

public class PotionSipperOverlay extends Overlay
{
	private final Client client;

	@Inject
	private PotionSipperOverlay(Client client) {
		this.client = client;
		setLayer(OverlayLayer.ALWAYS_ON_TOP);
		setPriority(OverlayPriority.HIGHEST);
		setPosition(OverlayPosition.DYNAMIC);
	}

	@Override
	public Dimension render(Graphics2D graphics) {

		graphics.setColor(Color.GREEN);
		graphics.drawLine(0, client.getMouseCanvasPosition().getY(), client.getCanvas().getWidth(), client.getMouseCanvasPosition().getY());
		graphics.drawLine(client.getMouseCanvasPosition().getX(), 0, client.getMouseCanvasPosition().getX(), client.getCanvas().getHeight());

		return null;
	}
}
