package qbitcraft.screen;

import qbitcraft.core.Game;
import qbitcraft.core.io.InputHandler;
import qbitcraft.entity.Entity;
import qbitcraft.entity.mob.Player;
import qbitcraft.item.Inventory;
import qbitcraft.item.Item;
import qbitcraft.item.StackableItem;
import qbitcraft.screen.entry.ItemEntry;

class InventoryMenu extends ItemListMenu {
	
	private Inventory inv;
	private Entity holder;
	
	InventoryMenu(Entity holder, Inventory inv, String title) {
		super(ItemEntry.useItems(inv.getItems()), title);
		this.inv = inv;
		this.holder = holder;
	}
	
	InventoryMenu(InventoryMenu model) {
		super(ItemEntry.useItems(model.inv.getItems()), model.getTitle());
		this.inv = model.inv;
		this.holder = model.holder;
		setSelection(model.getSelection());
	}
	
	@Override
	public void tick(InputHandler input) {
		super.tick(input);
		
		boolean dropOne = input.getKey("drop-one").clicked && !(Game.getMenu() instanceof ContainerDisplay);
		
		if(getNumOptions() > 0 && (dropOne || input.getKey("drop-stack").clicked)) {
			ItemEntry entry = ((ItemEntry)getCurEntry());
			if(entry == null) return;
			Item invItem = entry.getItem();
			Item drop = invItem.clone();
			
			if(dropOne && drop instanceof StackableItem && ((StackableItem)drop).count > 1) {
				// just drop one from the stack
				((StackableItem)drop).count = 1;
				((StackableItem)invItem).count--;
			} else {
				// drop the whole item.
				if(!Game.isMode("creative") || !(holder instanceof Player))
					removeSelectedEntry();
			}
			
			if(holder.getLevel() != null) {
				if(Game.isValidClient())
					Game.client.dropItem(drop);
				else
					holder.getLevel().dropItem(holder.x, holder.y, drop);
			}
		}
	}
	
	@Override
	public void removeSelectedEntry() {
		inv.remove(getSelection());
		super.removeSelectedEntry();
	}
}
