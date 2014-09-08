package fr.inria.arles.callouts;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import fr.inria.arles.yarta.resources.Agent;
import fr.inria.arles.yarta.resources.Content;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CalloutsListAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private List<Content> callouts = new ArrayList<Content>();
	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM, HH:mm",
			Locale.getDefault());

	private static class ViewHolder {
		TextView author;
		TextView text;
		TextView time;
	}

	public CalloutsListAdapter(Context context) {
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return callouts.size();
	}

	@Override
	public Object getItem(int position) {
		return callouts.get(position);
	}

	@Override
	public long getItemId(int position) {
		return getItem(position).hashCode();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater
					.inflate(R.layout.item_callout, parent, false);

			holder = new ViewHolder();
			holder.author = (TextView) convertView.findViewById(R.id.author);
			holder.text = (TextView) convertView.findViewById(R.id.title);
			holder.time = (TextView) convertView.findViewById(R.id.time);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Content content = (Content) getItem(position);

		for (Agent agent : content.getCreator_inverse()) {
			holder.author.setText(agent.getName());
		}

		holder.text.setText(content.getTitle());

		try {
			holder.time.setText(sdf.format(new Date(content.getTime())));
		} catch (NumberFormatException ex) {
		}

		return convertView;
	}

	public void setItems(Set<Content> items) {
		this.callouts.clear();
		this.callouts.addAll(items);
		notifyDataSetChanged();
	}

}
