package fr.inria.arles.yarta.android.library.util;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class GenericPageAdapter extends FragmentStatePagerAdapter {

	public GenericPageAdapter(FragmentManager fm, Context context) {
		super(fm);
		this.context = context;
	}

	public void addFragment(Fragment fragment, int resTitleId) {
		fragments.add(fragment);
		fragmentTitles.add(context.getString(resTitleId));
	}

	@Override
	public Fragment getItem(int location) {
		return fragments.get(location);
	}

	@Override
	public int getCount() {
		return fragments.size();
	}

	@Override
	public CharSequence getPageTitle(int location) {
		return fragmentTitles.get(location);
	}

	private Context context;
	private List<Fragment> fragments = new ArrayList<Fragment>();
	private List<String> fragmentTitles = new ArrayList<String>();
}
