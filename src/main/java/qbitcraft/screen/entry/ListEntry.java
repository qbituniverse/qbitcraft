package qbitcraft.screen.entry;

import qbitcraft.core.io.InputHandler;
import qbitcraft.gfx.Color;
import qbitcraft.gfx.Font;
import qbitcraft.gfx.Screen;

public abstract class ListEntry {
	
	public static final int COL_UNSLCT = Color.GRAY;
	public static final int COL_SLCT = Color.WHITE;
	
	private boolean selectable = true, visible = true;
	
	/**
	 * Ticks the entry. Used to handle input from the InputHandler
	 * @param input InputHandler used to get player input.
	 */
	public abstract void tick(InputHandler input);
	
	/**
	 * Renders the entry to the given screen.
	 * Coordinate origin is in the top left corner of the entry space.
	 * @param screen Screen to render the entry to
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param isSelected true if the entry is selected, false otherwise
	 */
	public void render(Screen screen, int x, int y, boolean isSelected) {
		if(visible)
			Font.draw(toString(), screen, x, y, getColor(isSelected));
	}
	
	/**
	 * Returns the current color depending on if the entry is selected.
	 * @param isSelected true if the entry is selected, false otherwise
	 * @return the current entry color
	 */
	public int getColor(boolean isSelected) { return isSelected ? COL_SLCT : COL_UNSLCT; }
	
	/**
	 * Calculates the width of the entry.
	 * @return the entry's width
	 */
	public int getWidth() {
		return Font.textWidth(toString());
	}
	
	/**
	 * Calculates the height of the entry.
	 * @return the entry's height
	 */
	public static int getHeight() {
		return Font.textHeight();
	}
	
	/**
	 * Determines if this entry can be selected.
	 * @return true if it is visible and can be selected, false otherwise.
	 */
	public final boolean isSelectable() { return selectable && visible; }
	
	/**
	 * Returns whether the entry is visible or not.
	 * @return true if the entry is visible, false otherwise
	 */
	public final boolean isVisible() { return visible; }
	
	/**
	 * Changes if the entry can be selected or not.
	 * @param selectable true if the entry can be selected, false if not
	 */
	public final void setSelectable(boolean selectable) { this.selectable = selectable; }
	
	/**
	 * Changes if the entry is visible or not.
	 * @param visible true if the entry should be visible, false if not
	 */
	public final void setVisible(boolean visible) { this.visible = visible; }
	
	@Override
	public abstract String toString();
}
