package fr.inria.arles.foosball;

import java.util.ArrayList;
import java.util.List;

import fr.inria.arles.foosball.resources.Player;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SimplePlayerListAdapter extends BaseAdapter {

	private class ViewHolder {
		TextView name;
	}

	private Context context;
	private List<Player> players;

	public SimplePlayerListAdapter(Context context) {
		this.context = context;
		players = new ArrayList<Player>();
	}

	@Override
	public int getCount() {
		return players.size();
	}

	@Override
	public Object getItem(int position) {
		return players.get(position);
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
					R.layout.item_player_simple, parent, false);

			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.name);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		String nickName = player.getNickName();

		if (nickName == null) {
			nickName = player.getUserId().replace("@inria.fr", "");
		}
		holder.name.setText(nickName);

		return convertView;
	}

	public void setItems(List<Player> players) {
		this.players.clear();
		this.players.addAll(players);
		notifyDataSetChanged();
	}
}
