package fr.inria.arles.giveaway;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import fr.inria.arles.giveaway.resources.Announcement;
import fr.inria.arles.giveaway.resources.Donation;
import fr.inria.arles.giveaway.resources.PersonImpl;
import fr.inria.arles.giveaway.resources.Request;
import fr.inria.arles.giveaway.resources.Sale;
import fr.inria.arles.giveaway.util.JobRunner.Job;
import fr.inria.arles.yarta.resources.Person;
import fr.inria.arles.yarta.resources.YartaResource;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

public class NewsFragment extends BaseFragment implements
		NewsListAdapter.Handler, AdapterView.OnItemClickListener {

	private Person me;
	private NewsListAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_news, container, false);

		adapter = new NewsListAdapter(getActivity(), this);

		ListView list = (ListView) root.findViewById(R.id.listNews);
		list.setAdapter(adapter);
		list.setEmptyView(root.findViewById(R.id.listEmpty));
		list.setOnItemClickListener(this);
		return root;
	}

	@Override
	public void refreshUI() {
		execute(new Job() {

			List<Announcement> announcements;

			@Override
			public void doWork() {
				try {
					me = getSAM().getMe();
					announcements = getAnnouncements();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

			@Override
			public void doUIAfter() {
				if (announcements != null) {
					adapter.setItems(announcements);
				}
			}
		});
	}

	private List<Announcement> getAnnouncements() {
		List<Announcement> list = new ArrayList<Announcement>();

		switch (filterType) {
		case FILTER_DONATIONS:
			for (Donation d : getSAM().getAllDonations()) {
				if (d.getHiddenBy().isEmpty()) {
					list.add(d);
				}
			}
			break;
		case FILTER_REQUESTS:
			for (Request r : getSAM().getAllRequests()) {
				if (r.getHiddenBy().isEmpty()) {
					list.add(r);
				}
			}
			break;
		case FILTER_MINE:
			for (Announcement a : getSAM().getAllAnnouncements()) {
				if (a.getCreator_inverse().contains(me)
						&& a.getHiddenBy().isEmpty()) {
					list.add(a);
				}
			}
			break;
		case FILTER_SALES:
			for (Sale s : getSAM().getAllSales()) {
				if (s.getHiddenBy().isEmpty()) {
					list.add(s);
				}
			}
			break;
		}

		Collections.sort(list, comparator);
		return list;
	}

	private Comparator<Announcement> comparator = new Comparator<Announcement>() {
		@Override
		public int compare(Announcement lhs, Announcement rhs) {
			try {
				return (int) (rhs.getTime() - lhs.getTime());
			} catch (Exception ex) {
				return 0;
			}
		}
	};

	@Override
	public void onClick(View view, int position) {
		final Announcement announcement = (Announcement) adapter
				.getItem(position);

		AlertDialog.show(getSherlockActivity(),
				getString(R.string.wire_remove_message),
				getString(R.string.wire_remove_title),
				getString(R.string.wire_remove_yes),
				getString(R.string.wire_remove_cancel),
				new AlertDialog.Handler() {

					@Override
					public void onOK() {
						onHideItem(announcement);
					}
				});
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		onViewItem((Announcement) adapter.getItem(position));
	}

	private void onViewItem(Announcement announcement) {
		Intent intent = new Intent(getActivity(), ItemActivity.class);

		if (announcement instanceof Donation) {
			intent.putExtra(ItemActivity.DonationId, announcement.getUniqueId());
		} else if (announcement instanceof Request) {
			intent.putExtra(ItemActivity.RequestId, announcement.getUniqueId());
		} else if (announcement instanceof Sale) {
			intent.putExtra(ItemActivity.SaleId, announcement.getUniqueId());
		}

		startActivity(intent);
	}

	private void onHideItem(Announcement announcement) {
		announcement.addHiddenBy(new PersonImpl(getSAM(), ((YartaResource) me)
				.getNode()));
		refreshUI();
		sendNotify();
	}
}
