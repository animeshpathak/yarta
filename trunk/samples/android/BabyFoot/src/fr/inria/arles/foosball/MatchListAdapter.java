package fr.inria.arles.foosball;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import fr.inria.arles.foosball.resources.Match;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MatchListAdapter extends BaseAdapter {

	private List<Match> matches = new ArrayList<Match>();
	private Context context;

	private class ViewHolder {
		TextView name;
		TextView info;
	}

	public MatchListAdapter(Context context) {
		this.context = context;
	}

	@Override
	public int getCount() {
		return matches.size();
	}

	public Object getItem(int position) {
		return matches.get(position);
	}

	@Override
	public long getItemId(int position) {
		return getItem(position).hashCode();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;

		Match match = (Match) getItem(position);

		if (match == null) {
			return convertView;
		}

		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_match, parent, false);

			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.info = (TextView) convertView.findViewById(R.id.info);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		try {
			holder.name.setText(context.getString(R.string.games_final_score)
					+ match.getBlueScore() + " - " + match.getRedScore());
		} catch (Exception ex) {
			holder.name.setText("not yet finished");
		}
		holder.info.setText(context.getString(R.string.games_play_time)
				+ sdf.format(new Date(match.getTime())));
		return convertView;
	}

	public void setMatches(List<Match> matches) {
		this.matches.clear();
		this.matches.addAll(matches);
		notifyDataSetChanged();
	}

	private SimpleDateFormat sdf = new SimpleDateFormat("hh:mm, MM/dd/yyy",
			Locale.US);
}
