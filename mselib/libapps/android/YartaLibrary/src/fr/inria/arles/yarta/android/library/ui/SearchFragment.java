package fr.inria.arles.yarta.android.library.ui;

import java.util.ArrayList;
import java.util.List;

import com.astuetz.PagerSlidingTabStrip;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import fr.inria.arles.iris.R;
import fr.inria.arles.iris.web.ObjectItem;
import fr.inria.arles.yarta.android.library.util.BaseFragment;
import fr.inria.arles.yarta.android.library.util.JobRunner.Job;

public class SearchFragment extends BaseFragment {

	private class SearchResultsAdapter extends FragmentPagerAdapter {

		private int[] PageTitles = new int[] { R.string.search_users,
				R.string.search_groups, R.string.search_blogs };
		private String[] PageTypes = new String[] { ObjectItem.User,
				ObjectItem.Group, ObjectItem.Blog };

		public SearchResultsAdapter(FragmentManager fm) {
			super(fm);
			fragments = new ArrayList<SearchResultsFragment>();
		}

		@Override
		public Fragment getItem(int position) {
			SearchResultsFragment fragment = new SearchResultsFragment();
			fragment.setRunner(runner);
			fragment.setType(PageTypes[position]);
			fragment.setData(items);
			fragments.add(fragment);
			return fragment;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return getString(PageTitles[position]);
		}

		@Override
		public int getCount() {
			return PageTitles.length;
		}
	}

	private PagerSlidingTabStrip tabs;
	private ViewPager pager;
	private List<ObjectItem> items;
	private List<SearchResultsFragment> fragments;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_search, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		tabs = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
		pager = (ViewPager) view.findViewById(R.id.pager);

		pager.setAdapter(new SearchResultsAdapter(getChildFragmentManager()));
		tabs.setViewPager(pager);
		tabs.clearFocus();
	}

	@Override
	public void refreshUI() {
		for (SearchResultsFragment fragment : fragments) {
			fragment.setData(items);
		}
	}

	public void search(final String query) {
		runner.runBackground(new Job() {
			@Override
			public void doWork() {
				items = client.search(query);
			}

			@Override
			public void doUIAfter() {
				refreshUI();
			}
		});
	}
}
