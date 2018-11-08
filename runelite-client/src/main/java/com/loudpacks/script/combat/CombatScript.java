package com.loudpacks.script.combat;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.loudpacks.script.ApiProvider;
import com.loudpacks.script.Script;

public class CombatScript extends Script
{

    @Inject
    public CombatScript(ApiProvider api, EventBus eventBus) {
    	super(api, eventBus);
    	start();
    }

    @Override
    public void start() {
        super.start();
        addTask(new GenericFightTask(api, eventBus));
    }


}

