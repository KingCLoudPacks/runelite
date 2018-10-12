package com.loudpacks.script.flickscript;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.loudpacks.script.ApiProvider;
import com.loudpacks.script.Script;

public class FlickScript extends Script {

    @Inject
    public FlickScript(ApiProvider api, EventBus eventBus) {
        super(api, eventBus);
    }

    @Override
    public void start() {
        super.start();
        addTask(new FlickTask(this, api, eventBus));
    }


}
