package fr.inria.arles.yarta.android.library;

import fr.inria.arles.yarta.android.library.util.BaseFragment;

public class SideMenuItem {

	private String menuText;
	private int iconResId;
	private BaseFragment fragment;

	public SideMenuItem(String menuText, int iconResId, BaseFragment fragment) {
		this.menuText = menuText;
		this.iconResId = iconResId;
		this.fragment = fragment;
	}

	public int getIconResId() {
		return iconResId;
	}

	public BaseFragment getFragment() {
		return fragment;
	}

	public String getText() {
		return menuText;
	}
}
