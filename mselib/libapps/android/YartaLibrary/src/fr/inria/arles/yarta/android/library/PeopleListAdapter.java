package fr.inria.arles.yarta.android.library;

import java.util.ArrayList;
import java.util.List;

import fr.inria.arles.iris.R;
import fr.inria.arles.yarta.resources.Person;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PeopleListAdapter extends BaseAdapter {

	private class ViewHolder {
		TextView name;
		TextView info;
	}

	private Context context;

	public PeopleListAdapter(Context context) {
		this.context = context;
	}

	@Override
	public int getCount() {
		return agents.size();
	}

	@Override
	public Object getItem(int position) {
		return agents.get(position);
	}

	@Override
	public long getItemId(int position) {
		return getItem(position).hashCode();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		Person person = (Person) getItem(position);

		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_person, parent, false);

			holder = new ViewHolder();
			holder.info = (TextView) convertView.findViewById(R.id.info);
			holder.name = (TextView) convertView.findViewById(R.id.name);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		String shortId = person.getUserId().replace("@inria.fr", "");
		String email = person.getEmail();

		if (email == null || email.length() == 0) {
			email = context.getString(R.string.people_person_n_a);
		}

		holder.name.setText(shortId);
		holder.info.setText(email);
		return convertView;
	}

	public void setPeople(List<Person> agents) {
		this.agents.clear();
		this.agents.addAll(agents);
		notifyDataSetChanged();
	}

	private List<Person> agents = new ArrayList<Person>();
}
