package com.loudpacks.script.flickscript;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.loudpacks.script.ApiProvider;
import com.loudpacks.script.ConditionalSleep;
import com.loudpacks.script.Script;
import com.loudpacks.script.ScriptTask;
import java.awt.Graphics2D;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import net.runelite.api.GameState;
import net.runelite.api.Point;
import net.runelite.api.Prayer;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;

public class FlickTask extends ScriptTask {

    private LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>();


    public FlickTask(Script script, ApiProvider api, EventBus eventBus) {
        super(script, api, eventBus);
        eventBus.register(this);
    }

    @Override
    public boolean isActive() {
        return api.client.getGameState().equals(GameState.LOGGED_IN);
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onLoop() {
        /*
        try {
            String tick = queue.take();
            Widget prayer = api.client.getWidget(WidgetInfo.MINIMAP_QUICK_PRAYER_ORB_PRECISE);
            if (prayer != null) {
                Point origin = prayer.getCanvasLocation();
                Point toClick = new Point(ThreadLocalRandom.current().nextInt(origin.getX() + 1, origin.getX() + prayer.getWidth()), ThreadLocalRandom.current().nextInt(origin.getY() + 1, origin.getY() + prayer.getHeight()));
                api.getInputHandler().leftClick(toClick);
               new ConditionalSleep(1000, 10) {
                   @Override
                   public boolean condition()
                   {
                       return api.client.getLocalPlayer().getOverheadIcon() != null && api.client.isPrayerActive(Prayer.PROTECT_FROM_MELEE);
                   }
               }.sleep();
                api.getInputHandler().leftClick(toClick);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        */
    }

    @Override
    public void onEnd() {

    }

    @Override
    public void onPaint(Graphics2D g) {

    }

    @Subscribe
    private void onTick(GameTick event) {
        if (script.isRunning()) {
            // queue.add(event.toString());
            Thread t = new Thread(() -> {
                Widget prayer = api.client.getWidget(WidgetInfo.MINIMAP_QUICK_PRAYER_ORB_PRECISE);
                if (prayer != null) {
                    Point origin = prayer.getCanvasLocation();
                    Point toClick = new Point(ThreadLocalRandom.current().nextInt(origin.getX() + 1, origin.getX() + prayer.getWidth()), ThreadLocalRandom.current().nextInt(origin.getY() + 1, origin.getY() + prayer.getHeight()));
                    api.getInputHandler().leftClick(toClick);
                    new ConditionalSleep(1000, 10) {
                        @Override
                        public boolean condition() {
                            return isPrayerActive();
                        }
                    }.sleep();
                    api.getInputHandler().leftClick(toClick);
                }
            });
            t.start();
        }
    }

    private boolean isPrayerActive() {
        for (Prayer p : Prayer.values()) {
            if (api.client.isPrayerActive(p))
                return true;
        }
        return false;
    }

}