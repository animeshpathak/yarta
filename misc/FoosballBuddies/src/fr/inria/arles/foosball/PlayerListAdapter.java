package fr.inria.arles.foosball;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fr.inria.arles.foosball.util.FoosballCore;
import fr.inria.arles.foosball.util.Player;

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
	private List<Player> persons;
	private Map<String, Integer> playedMatches;

	public PlayerListAdapter(Context context) {
		this.context = context;
		persons = new ArrayList<Player>();
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
		Player player = (Player) getItem(position);

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

		holder.name.setText(player.getUserName());

		Integer played = FoosballCore.getInstance().fastGetTotalGames(
				player.getUserId());
		if (playedMatches != null) {
			played = playedMatches.get(player.getUserId());
		}

		holder.info.setText(String.format(
				context.getString(R.string.main_player_matches_played),
				played == null ? 0 : played));
		return convertView;
	}

	public void setItems(List<Player> persons) {
		this.persons.clear();
		this.persons.addAll(persons);
		notifyDataSetChanged();
	}

	public void setMatches(Map<String, Integer> playedMatches) {
		this.playedMatches = playedMatches;
		notifyDataSetChanged();
	}
}
