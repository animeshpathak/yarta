package fr.inria.arles.yarta.android.library.util;

import java.util.ArrayList;
import java.util.List;

import com.astuetz.PagerSlidingTabStrip.IconTabProvider;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

public class IconPageAdapter extends GenericPageAdapter implements
		IconTabProvider {

	public IconPageAdapter(FragmentManager fm, Context context) {
		super(fm, context);
	}

	public void addFragment(Fragment fragment, int resTitleId, int iconId) {
		super.addFragment(fragment, resTitleId);
		fragmentIcons.add(iconId);
	}

	@Override
	public int getPageIconResId(int position) {
		if (position < fragmentIcons.size() && position > -1) {
			return fragmentIcons.get(position);
		}
		return 0;
	}

	private List<Integer> fragmentIcons = new ArrayList<Integer>();
}
