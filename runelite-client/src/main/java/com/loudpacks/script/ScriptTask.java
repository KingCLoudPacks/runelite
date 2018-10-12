package com.loudpacks.script;

import com.google.common.eventbus.EventBus;
import java.awt.Graphics2D;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class ScriptTask {

    protected final ApiProvider api;
    protected final EventBus eventBus;
    protected final Script script;
    private boolean hasStarted = false;

    public ScriptTask(Script script, ApiProvider api, EventBus eventBus) {
        this.script = script;
        this.api = api;
        this.eventBus = eventBus;
        eventBus.register(api);
    }

    public abstract boolean isActive();

    public abstract void onStart();

    public abstract void onLoop();

    public abstract void onEnd();

    public abstract void onPaint(Graphics2D g);

    public boolean hasStarted() {
        return hasStarted;
    }

    public void setStarted(boolean b) {
        hasStarted = b;
    }

}
