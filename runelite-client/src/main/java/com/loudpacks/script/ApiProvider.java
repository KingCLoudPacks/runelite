package com.loudpacks.script;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import javax.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.VarClientStr;
import net.runelite.api.queries.InventoryWidgetItemQuery;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.itemstats.ItemStatChanges;
import net.runelite.client.util.QueryRunner;


@Slf4j
public class ApiProvider {

    @Inject
    public Client client;

    @Getter
    public InputHandler inputHandler = new InputHandler();

	@Inject
	public ItemStatChanges statChanges;

	@Getter
	public Inventory inventory = new Inventory();

	@Getter
	public Prayer prayer = new Prayer();

	@Inject
	public ItemManager itemManager;

	@Inject
	public QueryRunner queryRunner;


    @Inject
    public ApiProvider() {

    }

    public class Prayer
	{
		public Prayer() { }

		public boolean isOpen() {
			return client.getIntVarcs()[171] == 5;
		}

		public boolean open() {
			Widget prayerTab = client.getWidget((client.isResized()) ? WidgetInfo.RESIZABLE_VIEWPORT_PRAYER_TAB : WidgetInfo.FIXED_VIEWPORT_PRAYER_TAB);
			if (prayerTab != null && !prayerTab.isHidden()) {
				Rectangle rect = prayerTab.getBounds();
				inputHandler.leftClick(rect);
				new ConditionalSleep(1000, 150) {
					@Override
					public boolean condition() {
						return isOpen();
					}
				}.sleep();
				return true;
			}
			return false;
		}

		public boolean flickHeal()
		{
			Widget healPrayer = client.getWidget(WidgetInfo.PRAYER_RAPID_HEAL);
			if (healPrayer != null && !healPrayer.isHidden()) {
				Rectangle rect = healPrayer.getBounds();
				inputHandler.leftClick(rect);
				new ConditionalSleep(1000, ThreadLocalRandom.current().nextInt(350, 500)) {
					@Override
					public boolean condition() {
						return client.getVarbitValue(client.getVarps(), 4111) == 1;
					}
				}.sleep();
				inputHandler.leftClick(rect);
				new ConditionalSleep(1000, ThreadLocalRandom.current().nextInt(350, 500)) {
					@Override
					public boolean condition() {
						return client.getVarbitValue(client.getVarps(), 4111) == 0;
					}
				}.sleep();
				return true;
			}
			return false;
		}
	}

	public class Inventory {

		public Inventory() {

		}

		public int getSize() {
			final ItemContainer itemContainer = client.getItemContainer(InventoryID.INVENTORY);

			if (itemContainer == null) {
				return -1;
			}

			final Item[] items = itemContainer.getItems();

			int count = 0;

			for (int i = 0; i < items.length; i++) {
				if (items[i].getId() != -1)
					count++;

			}

			return count;
		}

		public boolean isFull() {
			return getSize() >= 28;
		}

		public boolean isOpen() {
			return client.getIntVarcs()[171] == 3;
		}

		public boolean open() {
			Widget inventoryTab = client.getWidget((client.isResized()) ? WidgetInfo.RESIZABLE_VIEWPORT_INVENTORY_TAB : WidgetInfo.FIXED_VIEWPORT_INVENTORY_TAB);
			if (inventoryTab != null && !inventoryTab.isHidden()) {
				Rectangle rect = inventoryTab.getBounds();
				inputHandler.leftClick(rect);
				new ConditionalSleep(1000, 150) {
					@Override
					public boolean condition() {
						return isOpen();
					}
				}.sleep();
				return true;
			}
			return false;
		}

		public boolean contains(int id) {
			Item[] inventory = client.getItemContainer(InventoryID.INVENTORY).getItems();
			Optional<Item> match = Arrays.asList(inventory).stream().filter(item -> item.getId() == id && item.getQuantity() > 0).findFirst();
			return match.isPresent();
		}

		public boolean contains(List<Integer> ids) {
			Item[] inventory = client.getItemContainer(InventoryID.INVENTORY).getItems();
			Optional<Item> match = Arrays.asList(inventory).stream().filter(item ->  ids.contains(item.getId()) && item.getQuantity() > 0).findFirst();
			return match.isPresent();
		}


		public WidgetItem getItem(int id) {
			List<WidgetItem> inventory = new ArrayList<>();
			inventory.addAll(Arrays.asList(queryRunner.runQuery(new InventoryWidgetItemQuery())));
			Optional<WidgetItem> match = inventory.stream().filter(item -> item.getId() == id && item.getQuantity() > 0).findFirst();
			return (match.isPresent()) ? match.get() : null;
		}

		public int getItemIndex(List<Integer> ids) {
			List<WidgetItem> inventory = new ArrayList<>();
			inventory.addAll(Arrays.asList(queryRunner.runQuery(new InventoryWidgetItemQuery())));
			Optional<WidgetItem> match = inventory.stream().filter(item -> ids.contains(item.getId()) && item.getQuantity() > 0).findFirst();
			WidgetItem item = match.orElse(null);
			return (item != null) ? item.getIndex() : -1;
		}

		public WidgetItem getItem(List<Integer> ids) {
			List<WidgetItem> inventory = new ArrayList<>();
			inventory.addAll(Arrays.asList(queryRunner.runQuery(new InventoryWidgetItemQuery())));
			Optional<WidgetItem> match = inventory.stream().filter(item -> ids.contains(item.getId()) && item.getQuantity() > 0).findFirst();
			return (match.isPresent()) ? match.get() : null;
		}

		public WidgetItem[] getItems() {
			List<WidgetItem> inventory = new ArrayList<>();
			inventory.addAll(Arrays.asList(queryRunner.runQuery(new InventoryWidgetItemQuery())));
			inventory.removeIf(w -> w.getId() == 6512 || w.getQuantity() <= 0);
			return inventory.toArray(new WidgetItem[inventory.size()]);
		}

		public boolean interact(int id, ConditionalSleep condition) {
			WidgetItem item = getItem(id);
			if (item != null) {
				inputHandler.leftClick(item.getCanvasBounds());
				condition.sleep();
				return true;
			}
			return false;
		}

		public boolean interact(List<Integer> ids, ConditionalSleep condition) {
			WidgetItem item = getItem(ids);
			if (item != null) {
				inputHandler.leftClick(item.getCanvasBounds());
				condition.sleep();
				return true;
			}
			return false;
		}


		public boolean interact(WidgetItem item, float xOffset, float yOffset, ConditionalSleep condition)
		{
			if (item != null) {
				inputHandler.leftClick(item.getCanvasBounds(), xOffset, yOffset);
				condition.sleep();
				return true;
			}
			return false;
		}
	}

    public class InputHandler
	{

		public InputHandler()
		{

		}

		private java.awt.Point generateIntegralPoint(Shape region, int dx, int dy)
		{
			Rectangle r = region.getBounds();
			r.translate(dx, dy);
			int x, y;
			do
			{
				x = (int) Math.round(ThreadLocalRandom.current().nextDouble(r.getMinX() + 1, r.getMaxX() - 1));
				y = (int) Math.round(ThreadLocalRandom.current().nextDouble(r.getMinY() + 1, r.getMaxY() - 1));
			} while (!region.contains(x, y));
			return new java.awt.Point(x, y);
		}

		public void moveMouse(int x, int y)
		{

			if (!isOnCanvas(client.getMouseCanvasPosition().getX(), client.getMouseCanvasPosition().getY()) && isOnCanvas(x, y))
			{
				MouseEvent mouseEntered = new MouseEvent(client.getCanvas(), MouseEvent.MOUSE_ENTERED, System.currentTimeMillis(), 0, x,
					y, 0, false);
				client.getCanvas().dispatchEvent(mouseEntered);
			}

			if (isOnCanvas(client.getMouseCanvasPosition().getX(), client.getMouseCanvasPosition().getY()) && !isOnCanvas(x, y))
			{
				MouseEvent mouseExited = new MouseEvent(client.getCanvas(), MouseEvent.MOUSE_EXITED, System.currentTimeMillis(), 0, x,
					y, 0, false);
				client.getCanvas().dispatchEvent(mouseExited);
			}

			if (isOnCanvas(client.getMouseCanvasPosition().getX(), client.getMouseCanvasPosition().getY()) && isOnCanvas(x, y))
			{
				MouseEvent mouseMoved = new MouseEvent(client.getCanvas(), MouseEvent.MOUSE_MOVED, System.currentTimeMillis(), 0, x,
					y, 0, false);
				client.getCanvas().dispatchEvent(mouseMoved);
			}
		}

		public void leftClick()
		{
			MouseEvent mousePressed = new MouseEvent(client.getCanvas(), MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(), 0, client.getMouseCanvasPosition().getX(), client.getMouseCanvasPosition().getY(),
				1, false, MouseEvent.BUTTON1);
			client.getCanvas().dispatchEvent(mousePressed);

			MouseEvent mouseReleased = new MouseEvent(client.getCanvas(), MouseEvent.MOUSE_RELEASED, System.currentTimeMillis(), 0, client.getMouseCanvasPosition().getX(), client.getMouseCanvasPosition().getY(),
				1, false, MouseEvent.BUTTON1);
			client.getCanvas().dispatchEvent(mouseReleased);

			MouseEvent mouseClicked = new MouseEvent(client.getCanvas(), MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), 0, client.getMouseCanvasPosition().getX(), client.getMouseCanvasPosition().getY(),
				1, false, MouseEvent.BUTTON1);
			client.getCanvas().dispatchEvent(mouseClicked);
		}

		private boolean isOnCanvas(final int x, final int y)
		{
			return x > 0 && x < client.getCanvas().getWidth() && y > 0 && y < client.getCanvas().getHeight();
		}

		public void leftClick(Rectangle rect)
		{
			if (rect != null)
			{
				int rx = (int) Math.round(ThreadLocalRandom.current().nextDouble(rect.getMinX() + 1, rect.getMaxX() - 1));
				int ry = (int) Math.round(ThreadLocalRandom.current().nextDouble(rect.getMinY() + 1, rect.getMaxY() - 1));
				moveMouse(rx, ry);
				leftClick();
			}
		}

		public void leftClick(Rectangle rect, float xOffset, float yOffset)
		{
			if (rect != null)
			{
				int rx = (int) rect.getMinX() + 1 + (int) (xOffset * rect.getWidth());
				int ry = (int) rect.getMinY() + 1 + (int) (yOffset * rect.getHeight());
				moveMouse(rx, ry);
				leftClick();
			}
		}
	}
}


