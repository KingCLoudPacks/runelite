package net.runelite.client.plugins.prayerflick;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.awt.event.MouseEvent;
import java.util.concurrent.ThreadLocalRandom;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Point;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;

public class ActionThread extends Thread {

    @Setter
    private boolean running = true;

    private Client client;
    private EventBus eventBus;

    public ActionThread(EventBus eventBus, Client client) {
        this.client = client;
        this.eventBus = eventBus;
        eventBus.register(this);
    }

    @Override
    public void run() {
        super.run();
    }

    @Subscribe
    private void onTick(GameTick event) {
        if (running) {
            if (client.getGameState().equals(GameState.LOGGED_IN)) {
                Widget prayer = client.getWidget(WidgetInfo.MINIMAP_QUICK_PRAYER_ORB);
            if (prayer != null) {
                Point origin = prayer.getCanvasLocation();
                Point toClick = new Point(ThreadLocalRandom.current().nextInt(origin.getX() + 1, origin.getX() + prayer.getWidth()), ThreadLocalRandom.current().nextInt(origin.getY() + 1, origin.getY() + prayer.getHeight()));
                sendMouseClick(toClick.getX(), toClick.getY());
                try {
                    Thread.sleep(ThreadLocalRandom.current().nextInt(50, 150));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                sendMouseClick(toClick.getX(), toClick.getY());

            }
        }
        }
    }

    private void sendMouseClick(int x, int y) {
        MouseEvent mousePressed = new MouseEvent(client.getCanvas(), MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(), 0, x, y,
                1, false, MouseEvent.BUTTON1);
        client.getCanvas().dispatchEvent(mousePressed);

        MouseEvent mouseReleased = new MouseEvent(client.getCanvas(), MouseEvent.MOUSE_RELEASED, System.currentTimeMillis(), 0, x, y,
                1, false, MouseEvent.BUTTON1);
        client.getCanvas().dispatchEvent(mouseReleased);

        MouseEvent mouseClicked = new MouseEvent(client.getCanvas(), MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), 0, x, y,
                1, false, MouseEvent.BUTTON1);
        client.getCanvas().dispatchEvent(mouseClicked);
    }

}
