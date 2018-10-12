package net.runelite.client.plugins.safespot;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Provides;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import javax.inject.Inject;
import lombok.Getter;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.CollisionData;
import net.runelite.api.Constants;
import static net.runelite.api.Constants.CHUNK_SIZE;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.api.Scene;
import net.runelite.api.Tile;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.InteractingChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(
        name = "Safespot Tiles",
        description = "Highlights potential safespotting tiles against frozen players",
        tags = {"safespot", "pk", "safe", "loudpacks"}
)

public class SafespotPlugin extends Plugin {

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private Client client;
    @Inject
    private SafespotOverlay overlay;
    @Inject
    private SafespotConfig config;

    @Getter
    private List<WorldPoint> tileList = new LinkedList<>();

    @Getter
    private Actor lastOpponent;

    private Instant lastTime;

    private WorldArea cachedTargetArea;

    @Provides
    SafespotConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(SafespotConfig.class);
    }


    private WorldPoint translateToWorld(SafeTilePoint point) {

        int regionId = point.getRegionId();
        int regionX = point.getRegionX();
        int regionY = point.getRegionY();
        int z = point.getZ();

        // world point of the tile marker
        WorldPoint worldPoint = new WorldPoint(
                ((regionId >>> 8) << 6) + regionX,
                ((regionId & 0xff) << 6) + regionY,
                z
        );

        return worldPoint;
    }


    private static WorldPoint rotateInverse(WorldPoint point, int rotation) {
        return rotate(point, 4 - rotation);
    }


    private static WorldPoint rotate(WorldPoint point, int rotation) {
        int chunkX = point.getX() & ~(CHUNK_SIZE - 1);
        int chunkY = point.getY() & ~(CHUNK_SIZE - 1);
        int x = point.getX() & (CHUNK_SIZE - 1);
        int y = point.getY() & (CHUNK_SIZE - 1);
        switch (rotation) {
            case 1:
                return new WorldPoint(chunkX + y, chunkY + (CHUNK_SIZE - 1 - x), point.getPlane());
            case 2:
                return new WorldPoint(chunkX + (CHUNK_SIZE - 1 - x), chunkY + (CHUNK_SIZE - 1 - y), point.getPlane());
            case 3:
                return new WorldPoint(chunkX + (CHUNK_SIZE - 1 - y), chunkY + x, point.getPlane());
        }
        return point;
    }


    @Override
    protected void startUp() throws Exception {
        overlayManager.add(overlay);
    }

    @Override
    protected void shutDown() throws Exception {
        overlayManager.remove(overlay);
    }

    @Subscribe
    public void onGameStateChange(GameStateChanged gameStateChanged) {
        if (gameStateChanged.getGameState() != GameState.LOGGED_IN) {
            return;
        }
        tileList.clear();
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

        if (lastOpponent != null) {

            if (cachedTargetArea == null || !lastOpponent.getWorldArea().equals(cachedTargetArea)) {
                cachedTargetArea = lastOpponent.getWorldArea();
                tileList.clear();
                onPositionChanged(lastOpponent.getWorldArea());
            }

            if (client.getLocalPlayer().getInteracting() == null && Duration.between(lastTime, Instant.now()).compareTo(Duration.ofSeconds(config.targetTimeout())) > 0) {
                lastOpponent = null;
                tileList.clear();
            }
        }
    }

    public void onPositionChanged(WorldArea tp) {
        for (int x = tp.getX() - config.tileRadius(); x <= tp.getX() + config.tileRadius(); x++) {
            for (int y = tp.getY() - config.tileRadius(); y <= tp.getY() + config.tileRadius(); y++) {
                WorldArea cTile = new WorldArea(x, y, 1, 1, client.getPlane());
                if (!tp.hasLineOfSightTo(client, cTile) && cTile.hasLineOfSightTo(client, tp) && isWalkable(cTile.toWorldPoint())) {
                    markTile(LocalPoint.fromWorld(client, cTile.toWorldPoint()));

                }
            }
        }
    }

    public boolean isWalkable(WorldPoint p) {
        final Scene scene = client.getScene();
        final Tile[][][] tiles = scene.getTiles();

        for (int x = 0; x < Constants.SCENE_SIZE; ++x) {
            for (int y = 0; y < Constants.SCENE_SIZE; ++y) {
                Tile tile = tiles[client.getPlane()][x][y];

                if (tile == null) {
                    continue;
                }
                if (p.equals(tile.getWorldLocation())) {

                    int checkX = tile.getSceneLocation().getX();
                    int checkY = tile.getSceneLocation().getY();

                    CollisionData[] collisionData = client.getCollisionMaps();
                    int[][] collisionDataFlags = collisionData[tile.getPlane()].getFlags();

                    return !(CollisionMask.checkMask(collisionDataFlags[checkX][checkY], CollisionMask.BLOCK_MOVEMENT_OBJECT)
                            || CollisionMask.checkMask(collisionDataFlags[checkX][checkY], CollisionMask.BLOCK_MOVEMENT_FLOOR)
                            || CollisionMask.checkMask(collisionDataFlags[checkX][checkY], CollisionMask.BLOCK_MOVEMENT_FLOOR_DECORATION)
                            || CollisionMask.checkMask(collisionDataFlags[checkX][checkY], CollisionMask.BLOCK_MOVEMENT_CLOSED));
                }
            }
        }
        return false;
    }

    protected void markTile(LocalPoint localPoint) {
        if (localPoint == null) {
            return;
        }

        WorldPoint worldPoint;

        if (client.isInInstancedRegion()) {
            // get position in the scene
            int sceneX = localPoint.getSceneX();
            int sceneY = localPoint.getSceneY();

            // get chunk from scene
            int chunkX = sceneX / CHUNK_SIZE;
            int chunkY = sceneY / CHUNK_SIZE;

            // get the template chunk for the chunk
            int[][][] instanceTemplateChunks = client.getInstanceTemplateChunks();
            int templateChunk = instanceTemplateChunks[client.getPlane()][chunkX][chunkY];

            int rotation = templateChunk >> 1 & 0x3;
            int templateChunkY = (templateChunk >> 3 & 0x7FF) * CHUNK_SIZE;
            int templateChunkX = (templateChunk >> 14 & 0x3FF) * CHUNK_SIZE;
            int plane = templateChunk >> 24 & 0x3;

            // calculate world point of the template
            int x = templateChunkX + (sceneX & (CHUNK_SIZE - 1));
            int y = templateChunkY + (sceneY & (CHUNK_SIZE - 1));

            worldPoint = new WorldPoint(x, y, plane);
            // rotate point back to 0, to match with template
            worldPoint = rotateInverse(worldPoint, rotation);
        } else {
            worldPoint = WorldPoint.fromLocal(client, localPoint);
        }

        int regionId = worldPoint.getRegionID();
        SafeTilePoint point = new SafeTilePoint(regionId, worldPoint.getX() & 0x3f, worldPoint.getY() & 0x3f, client.getPlane());
        WorldPoint wp = translateToWorld(point);
        tileList.add(wp);


    }


}
