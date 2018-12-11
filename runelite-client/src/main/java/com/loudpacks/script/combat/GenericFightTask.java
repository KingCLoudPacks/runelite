package com.loudpacks.script.combat;

import com.google.common.eventbus.EventBus;
import com.loudpacks.script.ApiProvider;
import com.loudpacks.script.ConditionalSleep;
import com.loudpacks.script.ScriptTask;
import java.awt.Graphics2D;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import net.runelite.api.GameState;
import net.runelite.api.InventoryID;
import net.runelite.api.ItemID;
import net.runelite.api.Point;
import net.runelite.api.Skill;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.plugins.itemstats.Effect;
import net.runelite.client.plugins.itemstats.StatChange;
import net.runelite.client.ui.overlay.OverlayUtil;

public class GenericFightTask extends ScriptTask
{
	private static final int ABSORPTION_VAR = 3956;
	private static final int OVERLOAD_VAR = 3955;
	private static final List<Integer> OVERLOADS = new ArrayList<>();
	private static final List<Integer> ABSORPTIONS = new ArrayList<>();
	private static int ABSORPTION_THRESH = ThreadLocalRandom.current().nextInt(400, 900);

	private Instant lastFlick = Instant.now();
	private int flickInterval = ThreadLocalRandom.current().nextInt(40, 55);

	private int deltaBoost = ThreadLocalRandom.current().nextInt(0, 3);
	private boolean caked = false;
	private int slot = api.getInventory().getItemIndex(ABSORPTIONS);
	private float xOffset = ThreadLocalRandom.current().nextFloat();
	private float yOffset = ThreadLocalRandom.current().nextFloat();
	private float rockX = ThreadLocalRandom.current().nextFloat();
	private float rockY = ThreadLocalRandom.current().nextFloat();


	public GenericFightTask(ApiProvider api, EventBus eventBus)
	{
		super(api, eventBus);
		eventBus.register(this);

		OVERLOADS.add(ItemID.OVERLOAD_1);
		OVERLOADS.add(ItemID.OVERLOAD_2);
		OVERLOADS.add(ItemID.OVERLOAD_3);
		OVERLOADS.add(ItemID.OVERLOAD_4);

		ABSORPTIONS.add(ItemID.ABSORPTION_1);
		ABSORPTIONS.add(ItemID.ABSORPTION_2);
		ABSORPTIONS.add(ItemID.ABSORPTION_3);
		ABSORPTIONS.add(ItemID.ABSORPTION_4);
	}

	@Override
	public boolean isActive()
	{
		return api.client.getGameState().equals(GameState.LOGGED_IN);
	}

	@Override
	public void onStart()
	{

	}

	@Override
	public void onLoop()
	{

		if(caked && !api.client.isInInstancedRegion())
		{
			caked = false;
			rockX = ThreadLocalRandom.current().nextFloat();
			rockY = ThreadLocalRandom.current().nextFloat();
		}

		if(api.client.getBoostedSkillLevel(Skill.HITPOINTS) == 1)
			caked = true;

		if (api.client.isInInstancedRegion() && api.client.getVarbitValue(api.client.getVarps(), OVERLOAD_VAR) == 0 && api.getInventory().contains(OVERLOADS))
		{
			if (!api.getInventory().isOpen())
			{
				api.getInventory().open();
			}
			else
			{
				api.getInventory().interact(OVERLOADS, new ConditionalSleep(5500, ThreadLocalRandom.current().nextInt(3000, 4500))
				{
					@Override
					public boolean condition()
					{
						return api.client.getVarbitValue(api.client.getVarps(), OVERLOAD_VAR) != 0;
					}
				});
			}
		}
		else if (api.client.isInInstancedRegion() && api.client.getVarbitValue(api.client.getVarps(), ABSORPTION_VAR) < ABSORPTION_THRESH && api.getInventory().contains(ABSORPTIONS))
		{
			if (!api.getInventory().isOpen())
			{
				api.getInventory().open();
			}
			else
			{
				while (api.client.getVarbitValue(api.client.getVarps(), ABSORPTION_VAR) < ABSORPTION_THRESH && api.getInventory().contains(ABSORPTIONS))
				{
						if(slot != api.getInventory().getItemIndex(ABSORPTIONS))
						{
							 slot = api.getInventory().getItemIndex(ABSORPTIONS);
							 xOffset = ThreadLocalRandom.current().nextFloat();
							 yOffset = ThreadLocalRandom.current().nextFloat();
							try
							{
								Thread.sleep(30);
							}
							catch (InterruptedException e)
							{
								e.printStackTrace();
							}
						}
						else
						{
							final int start = api.client.getVarbitValue(api.client.getVarps(), ABSORPTION_VAR);
							api.getInventory().interact(api.getInventory().getItem(ABSORPTIONS), xOffset, yOffset, new ConditionalSleep(2500, ThreadLocalRandom.current().nextInt(750, 1000))
							{
								@Override
								public boolean condition()
								{
									return api.client.getVarbitValue(api.client.getVarps(), ABSORPTION_VAR) > start;
								}
							});
						}
				}
				ABSORPTION_THRESH = ThreadLocalRandom.current().nextInt(400, 900);
			}
		}
		else if(api.client.isInInstancedRegion() && api.client.getBoostedSkillLevel(Skill.HITPOINTS) > 1 && api.getInventory().contains(ItemID.DWARVEN_ROCK_CAKE_7510) && !caked)
		{
			if (!api.getInventory().isOpen())
			{
				api.getInventory().open();
			}
			else
			{
					int hp = api.client.getBoostedSkillLevel(Skill.HITPOINTS);
					api.getInventory().interact(api.getInventory().getItem(ItemID.DWARVEN_ROCK_CAKE_7510), rockX, rockY, new ConditionalSleep(5500, ThreadLocalRandom.current().nextInt(650, 950))
					{
						@Override
						public boolean condition()
						{
							return api.client.getBoostedSkillLevel(Skill.HITPOINTS) != hp;
						}
					});
			}
		}
		else if (api.client.isInInstancedRegion() && api.client.getRealSkillLevel(Skill.PRAYER) >= 22 && api.client.getBoostedSkillLevel(Skill.PRAYER) > 0 && Duration.between(lastFlick, Instant.now()).getSeconds() >= flickInterval && api.client.isInInstancedRegion())
		{
			if (!api.getPrayer().isOpen())
			{
				api.getPrayer().open();
			}
			else
			{
				api.getPrayer().flickHeal();
				flickInterval = ThreadLocalRandom.current().nextInt(40, 55);
				lastFlick = Instant.now();
			}
		}

		else
		{
			for (WidgetItem item : api.getInventory().getItems())
			{
				Effect effect = api.statChanges.get(item.getId());
				if (effect != null)
				{
					StatChange change = effect.calculate(api.client).getStatChanges()[0];
					int realBoost = Integer.parseInt(change.getRelative().replace("+", ""));
					int maxBoost = Integer.parseInt(change.getTheoretical().replace("+", ""));
					if (realBoost >= maxBoost - deltaBoost)
					{
						Skill skill = Skill.valueOf(change.getStat().getName().toUpperCase());
						int initial = api.client.getBoostedSkillLevel(skill);
						api.getInventory().interact(item.getId(), new ConditionalSleep(2500, 1000)
						{
							@Override
							public boolean condition()
							{
								return api.client.getBoostedSkillLevel(skill) > initial;
							}
						});
						deltaBoost = ThreadLocalRandom.current().nextInt(0, 3);
					}
				}
			}
		}

	}

	@Override
	public void onEnd()
	{

	}

	@Override
	public void onPaint(Graphics2D g)
	{

	}

}
