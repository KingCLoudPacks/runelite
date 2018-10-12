package net.runelite.client.plugins.idlehandler;

import java.awt.event.KeyEvent;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
@PluginDescriptor(
        name = "Idle Handler",
        description = "Move camera periodically to prevent idle logout",
        tags = {"idle", "log", "loudpacks", "camera"},
        enabledByDefault = false
)
public class IdleHandlerPlugin extends Plugin {

    @Inject
    private Client client;
    private Thread t;

    private Instant lastMove = null;

    private long DELAY;

    private boolean running = true;

    @Override
    protected void startUp() throws Exception {
        DELAY = ThreadLocalRandom.current().nextLong(100000, 280000);
        t = new Thread(() -> {
            while (running) {
                if (client.getGameState().equals(GameState.LOGGED_IN) && (lastMove == null || Duration.between(lastMove, Instant.now()).toMillis() >= DELAY)) {

                    int h = ThreadLocalRandom.current().nextInt(2, 6);
                    int hKey = (h % 2 == 0) ? KeyEvent.VK_LEFT : KeyEvent.VK_RIGHT;
                    keyPress(hKey, h);

                    int v = ThreadLocalRandom.current().nextInt(2, 6);
                    int vKey = (v % 2 == 0) ? KeyEvent.VK_UP : KeyEvent.VK_DOWN;
                    keyPress(vKey, v);

                    DELAY = ThreadLocalRandom.current().nextLong(100000, 280000);
                    lastMove = Instant.now();
                }
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    @Override
    protected void shutDown() throws Exception {
        running = false;
        t.join();
    }

    private void keyPress(int code, int delay) {

        KeyEvent keyPressed = new KeyEvent(client.getCanvas(), KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, code);
        client.getCanvas().dispatchEvent(keyPressed);
        try {
            Thread.sleep(delay * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        KeyEvent keyTyped = new KeyEvent(client.getCanvas(), KeyEvent.KEY_TYPED, System.currentTimeMillis(), 0, KeyEvent.VK_UNDEFINED);
        client.getCanvas().dispatchEvent(keyTyped);
        KeyEvent keyReleased = new KeyEvent(client.getCanvas(), KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0, code);
        client.getCanvas().dispatchEvent(keyReleased);
    }

}

