package net.runelite.client.plugins.freezetimer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import javax.inject.Inject;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.util.ImageUtil;

public class FreezeTimerOverlay extends Overlay {

    private final BufferedImage freezeImg = ImageUtil.getResourceStreamFromClass(getClass(), "frozen.png");
    private final BufferedImage delayImg = ImageUtil.getResourceStreamFromClass(getClass(), "freeze_delay.png");
    private final HashSet<FreezeInstance> instances = new HashSet<>();

    @Inject
    private FreezeTimerOverlay() {
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.MED);
    }

    @Override
    public Dimension render(Graphics2D graphics) {

        instances.removeIf(i -> i.finished());
        instances.forEach(instance -> {
            renderInstance(graphics, instance);
        });

        return null;
    }

    public void addInstance(FreezeInstance instance) {
        instances.add(instance);
    }

    private void renderInstance(Graphics2D graphics, FreezeInstance instance) {
        final Player player = instance.getPlayer();
        if (player != null) {
            if (Duration.between(Instant.now(), instance.getExpire()).getSeconds() > 0) {
                String freezeText = String.format("%s", Duration.between(Instant.now(), instance.getExpire()).getSeconds());
                Point freezeLocation = player.getCanvasImageLocation(freezeImg, 2 * player.getModelHeight() / 3);
                Point freezeTextLocation = player.getCanvasTextLocation(graphics, freezeText, 2 * player.getModelHeight() / 3);
                if (freezeLocation != null) {
                    OverlayUtil.renderImageLocation(graphics, freezeLocation, freezeImg);
                    OverlayUtil.renderTextLocation(graphics, freezeTextLocation,  freezeText, Color.white);
                }
            } else if (Duration.between(Instant.now(), instance.getNext()).getSeconds() > 0) {
                String delayTxt = String.format("%s", Duration.between(Instant.now(), instance.getNext()).getSeconds());
                Point delayLocation = player.getCanvasImageLocation(delayImg, 2 * player.getModelHeight() / 3);
                Point delayTextLocation = player.getCanvasTextLocation(graphics, delayTxt, 2 * player.getModelHeight() / 3);
                if (delayLocation != null) {
                    OverlayUtil.renderImageLocation(graphics, delayLocation, delayImg);
                    OverlayUtil.renderTextLocation(graphics, delayTextLocation, delayTxt, Color.white);
                }
            }
        }
    }

}
