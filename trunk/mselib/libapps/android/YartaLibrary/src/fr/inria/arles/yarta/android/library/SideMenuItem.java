package fr.inria.arles.yarta.android.library;

import fr.inria.arles.yarta.android.library.util.BaseFragment;

public class SideMenuItem {

	private String menuText;
	private BaseFragment fragment;

	public SideMenuItem(String menuText, BaseFragment fragment) {
		this.menuText = menuText;
		this.fragment = fragment;
	}

	public BaseFragment getFragment() {
		return fragment;
	}

	public String getText() {
		return menuText;
	}
}
