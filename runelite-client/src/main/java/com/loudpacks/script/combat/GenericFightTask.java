package com.loudpacks.script.combat;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.google.common.eventbus.EventBus;
import com.loudpacks.script.ApiProvider;
import com.loudpacks.script.ConditionalSleep;
import com.loudpacks.script.ScriptTask;
import java.awt.Graphics2D;
import java.util.concurrent.ThreadLocalRandom;
import net.runelite.api.GameState;
import net.runelite.api.Skill;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.plugins.itemstats.Effect;
import net.runelite.client.plugins.itemstats.StatChange;

public class GenericFightTask extends ScriptTask
{
    private int deltaBoost = ThreadLocalRandom.current().nextInt(0, 3);

    public GenericFightTask(ApiProvider api, EventBus eventBus) {
        super(api, eventBus);
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
		for (WidgetItem item : api.getInventory().getItems()) {
			Effect effect = api.statChanges.get(item.getId());
			if (effect != null) {
				StatChange change = effect.calculate(api.client).getStatChanges()[0];
				int realBoost = Integer.parseInt(change.getRelative().replace("+", ""));
				int maxBoost = Integer.parseInt(change.getTheoretical().replace("+", ""));
				if (realBoost >= maxBoost - deltaBoost) {
					Skill skill = Skill.valueOf(change.getStat().getName().toUpperCase());
					int initial = api.client.getBoostedSkillLevel(skill);
					api.getInventory().interact(item.getId(), new ConditionalSleep(2500, 1000) {
						@Override
						public boolean condition() {
							return api.client.getBoostedSkillLevel(skill) > initial;
						}
					});
					deltaBoost = ThreadLocalRandom.current().nextInt(0, 3);
				}
			}
		}
    }

    @Override
    public void onEnd() {

    }

	@Override
	public void onPaint(Graphics2D g)
	{

	}

}
