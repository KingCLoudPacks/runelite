package net.runelite.client.plugins.prayerflick;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Provides;
import com.loudpacks.net.Request;
import com.loudpacks.net.socket.Server;
import com.loudpacks.script.ApiProvider;
import com.loudpacks.script.flickscript.FlickScript;
import javax.inject.Inject;
import lombok.Getter;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.api.Client;
import net.runelite.api.events.ConfigChanged;
import net.runelite.client.config.ConfigManager;

@PluginDescriptor(
        name = "Prayer Flick",
        description = "Flicks auto-prayer",
        tags = {"prayer", "tick", "flick", "auto", "loudpacks"}
)
public class PrayerFlickPlugin extends Plugin {

    @Inject
    private Client client;

    @Inject
    private ApiProvider api;

    @Inject
    private FlickScript flickScript;

    @Getter
    private Server server = null;

    private ActionThread flickThread;

    @Inject
    private EventBus eventBus;

    @Inject
    private PrayerFlickConfig config;

    @Provides
    PrayerFlickConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(PrayerFlickConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        if(config.serverEnabled()) {
            server = new Server(43594);
            server.start();
        }
        flickScript.start();
    }

    @Override
    protected void shutDown() throws Exception {
        flickScript.setRunning(false);
    }

    @Subscribe
    private void onConfigChanged(ConfigChanged event) {
        switch (event.getKey()) {
            case "running":
                if (server == null && Boolean.valueOf(event.getNewValue())) {
                    server = new Server(43594);
                    server.start();
                } else if(!Boolean.valueOf(event.getNewValue()) && server != null) {
                    server.stopServer();
                    server = null;
                }
                break;
            case "enabled":
                if (event.getNewValue().equals("true")) {
                   flickScript.setRunning(true);
                } else {
                    flickScript.setRunning(false);
                }
            break;
        }
    }

    public void sendServerRequest(Request request) {
        if (server != null && server.isRunning()) {
            server.sendRequestToClient(request);
        }
    }

}
