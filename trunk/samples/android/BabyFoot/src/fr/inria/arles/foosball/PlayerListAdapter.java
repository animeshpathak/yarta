package fr.inria.arles.foosball;

import java.util.ArrayList;
import java.util.List;

import fr.inria.arles.foosball.resources.Person;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PlayerListAdapter extends BaseAdapter {

	private class ViewHolder {
		TextView name;
		TextView info;
	}

	private Context context;
	private List<Person> persons;

	public PlayerListAdapter(Context context) {
		this.context = context;
		persons = new ArrayList<Person>();
	}

	@Override
	public int getCount() {
		return persons.size();
	}

	@Override
	public Object getItem(int position) {
		return persons.get(position);
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
					R.layout.item_player, parent, false);

			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.info = (TextView) convertView.findViewById(R.id.info);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		String nickName = person.getNickName();

		if (nickName == null) {
			nickName = person.getUserId().replace("@inria.fr", "");
		}
		holder.name.setText(nickName);

		int totalGames = 0;
		try {
			totalGames = person.getTotalGames();
		} catch (Exception ex) {
		}
		holder.info.setText(String.format(
				context.getString(R.string.main_player_matches_played),
				totalGames));

		return convertView;
	}

	public void setItems(List<Person> persons) {
		this.persons.clear();
		this.persons.addAll(persons);
		notifyDataSetChanged();
	}
}
