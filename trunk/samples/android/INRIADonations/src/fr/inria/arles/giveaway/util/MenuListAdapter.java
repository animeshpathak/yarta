package fr.inria.arles.giveaway.util;

import java.util.ArrayList;
import java.util.List;

import fr.inria.arles.giveaway.R;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MenuListAdapter extends BaseAdapter {

	class ViewHolder {
		TextView title;
		TextView item;
	}

	private List<SideMenuItem> items = new ArrayList<SideMenuItem>();
	private Context context;
	private LayoutInflater inflater;

	public MenuListAdapter(Context context, List<SideMenuItem> items) {
		this.context = context;
		this.items.addAll(items);
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return getItem(position).hashCode();
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;

		if (convertView == null) {
			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.item_drawer, parent, false);

			holder = new ViewHolder();
			holder.item = (TextView) convertView.findViewById(R.id.menuTitle);
			holder.title = (TextView) convertView.findViewById(R.id.titleTitle);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		SideMenuItem item = items.get(position);

		if (item.isSeparator()) {
			holder.item.setVisibility(View.GONE);
			holder.title.setVisibility(View.VISIBLE);
			holder.title.setText(item.getText());
		} else {
			holder.title.setVisibility(View.GONE);
			holder.item.setVisibility(View.VISIBLE);
			holder.item.setText(item.getText());
		}
		
		if (position == selectedPosition) {
			convertView.setBackgroundColor(context.getResources().getColor(R.color.AppDrawerSelected));
		} else {
			convertView.setBackgroundColor(Color.TRANSPARENT);
		}

		return convertView;
	}

	public void setSelected(int position) {
		this.selectedPosition = position;
		notifyDataSetChanged();
	}

	private int selectedPosition;
}