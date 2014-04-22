package fr.inria.arles.giveaway.util;

public class SideMenuItem {

	private String menuText;
	private int menuId;
	private boolean root;

	public SideMenuItem(String menuText, int menuId) {
		this.menuText = menuText;
		this.menuId = menuId;
	}

	public SideMenuItem(String menuText, int menuId, boolean root) {
		this.menuText = menuText;
		this.menuId = menuId;
		this.root = root;
	}

	public boolean isSeparator() {
		return root;
	}

	public String getText() {
		return menuText;
	}

	public int getMenuId() {
		return menuId;
	}
}
