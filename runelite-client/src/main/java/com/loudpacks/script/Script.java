package com.loudpacks.script;

import com.google.common.eventbus.EventBus;
import java.util.LinkedList;
import java.util.List;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;

public class Script extends Thread {

    private List<ScriptTask> taskQueue = new LinkedList<>();
    private boolean started = true;

    @Setter
    @Getter
    private boolean running = false;

    protected final ApiProvider api;
    protected final EventBus eventBus;

    @Inject
    public Script(ApiProvider api, EventBus eventBus) {
        this.api = api;
        this.eventBus = eventBus;
    }

    public ScriptTask getRunningTask() {
        for(ScriptTask t : taskQueue) {
            if(t.isActive())
                return t;
        }
        return null;
    }

    public void addTask(ScriptTask task) {
        taskQueue.add(task);
    }

    private int onLoop() {
        for (ScriptTask task : taskQueue) {
            if (task.isActive()) {
                if (task.hasStarted()) {
                    task.onLoop();
                } else {
                    task.onStart();
                    task.setStarted(true);
                }
            } else if (task.hasStarted()) {
                task.onEnd();
                task.setStarted(false);
            }
        }
        return 250;
    }

    public void stopScript() {
        this.started = false;
        try {
            join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        while (started) {
            if(running) {
                try {
                    sleep(onLoop());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
