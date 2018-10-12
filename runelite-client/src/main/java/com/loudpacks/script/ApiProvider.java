package com.loudpacks.script;

import com.loudpacks.net.Request;
import com.loudpacks.net.RequestType;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.concurrent.ThreadLocalRandom;
import javax.inject.Inject;
import javax.swing.SwingUtilities;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.plugins.prayerflick.PrayerFlickPlugin;


@Slf4j
public class ApiProvider {

    @Inject
    public Client client;

    @Getter
    public InputHandler inputHandler = new InputHandler();

    @Inject
    private PrayerFlickPlugin prayerFlickPlugin;


    @Inject
    public ApiProvider() {

    }


    public class InputHandler {

        public InputHandler() {

        }

        private void sendRequest(Request request) {
            prayerFlickPlugin.sendServerRequest(request);
        }

        private java.awt.Point generateIntegralPoint(Shape region, int dx, int dy) {
            Rectangle r = region.getBounds();
            r.translate(dx, dy);
            int x, y;
            do {
                x = (int) Math.round(ThreadLocalRandom.current().nextDouble(r.getMinX() + 1, r.getMaxX() - 1));
                y = (int) Math.round(ThreadLocalRandom.current().nextDouble(r.getMinY() + 1, r.getMaxY() - 1));
            } while (!region.contains(x, y));
            return new java.awt.Point(x, y);
        }

        public void leftClick(int x, int y) {
            Request request = new Request(RequestType.LEFT_CLICK);
            request.setParameter("x", String.valueOf(x));
            request.setParameter("y", String.valueOf(y));
            sendRequest(request);
            log.debug(String.format("Sending left click @ %d, %d", x, y));
        }

        public void leftClick(net.runelite.api.Point p) {
            java.awt.Point point = new java.awt.Point(p.getX(), p.getY());
            SwingUtilities.convertPointToScreen(point, client.getCanvas());
            leftClick(point.x, point.y);
        }

        public void sendKey(int vk, int direction) {
            Request request = new Request((direction == 0) ? RequestType.KEY_UP : RequestType.KEY_DOWN);
            request.setParameter("key", String.valueOf(vk));
            sendRequest(request);
            log.debug(String.format("Sending key %d - %s", vk, (direction == 0) ? "up" : "down"));
        }

        public void rightClick(int x, int y) {
            Request request = new Request(RequestType.RIGHT_CLICK);
            request.setParameter("x", String.valueOf(x));
            request.setParameter("y", String.valueOf(y));
            sendRequest(request);
            log.debug(String.format("Sending right click @ %d, %d", x, y));
        }

        public java.awt.Point convertToScreen(int x, int y) {
            java.awt.Point point = new java.awt.Point(x, y);
            SwingUtilities.convertPointToScreen(point, client.getCanvas());
            return point;
        }

        public void leftClick(Rectangle rect) {
            try {
                if (rect != null) {
                    int rx = (int) Math.round(ThreadLocalRandom.current().nextDouble(rect.getMinX() + 1, rect.getMaxX() - 1));
                    int ry = (int) Math.round(ThreadLocalRandom.current().nextDouble(rect.getMinY() + 1, rect.getMaxY() - 1));
                    java.awt.Point pt = convertToScreen(rx, ry);
                    leftClick((int) pt.getX(), (int) pt.getY());
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }

        public void leftClick(Polygon poly, int dx, int dy) {
            try {
                if (poly != null) {
                    java.awt.Point pt = generateIntegralPoint(poly, dx, dy);
                    SwingUtilities.convertPointToScreen(pt, client.getCanvas());
                    leftClick((int) pt.getX(), (int) pt.getY());
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }

        public void rightClick(Rectangle rect) {
            try {
                if (rect != null) {
                    int rx = (int) Math.round(ThreadLocalRandom.current().nextDouble(rect.getMinX() + 1, rect.getMaxX() - 1));
                    int ry = (int) Math.round(ThreadLocalRandom.current().nextDouble(rect.getMinY() + 1, rect.getMaxY() - 1));
                    java.awt.Point pt = convertToScreen(rx, ry);
                    rightClick((int) pt.getX(), (int) pt.getY());
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }

        public void rightClick(Polygon poly) {
            try {
                if (poly != null) {
                    java.awt.Point pt = generateIntegralPoint(poly, 0, 0);
                    SwingUtilities.convertPointToScreen(pt, client.getCanvas());
                    rightClick((int) pt.getX(), (int) pt.getY());
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }

        public void leftClick(Polygon poly) {
            try {
                if (poly != null) {
                    java.awt.Point pt = generateIntegralPoint(poly, 0, 0);
                    SwingUtilities.convertPointToScreen(pt, client.getCanvas());
                    leftClick((int) pt.getX(), (int) pt.getY());
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }
}


