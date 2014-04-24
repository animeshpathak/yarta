package fr.inria.arles.yarta.android.library;

import java.util.ArrayList;
import java.util.List;

import fr.inria.arles.iris.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MenuListAdapter extends BaseAdapter {

	class ViewHolder {
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

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		SideMenuItem item = items.get(position);

		holder.item.setText(item.getText());

		if (position == selectedPosition) {
			holder.item.setTextColor(context.getResources().getColor(
					R.color.AppDrawerTextSelected));
			convertView.setBackgroundColor(context.getResources().getColor(
					R.color.AppDrawerSelected));
		} else {
			holder.item.setTextColor(context.getResources().getColor(
					R.color.AppDrawerText));
			convertView.setBackgroundColor(context.getResources().getColor(
					R.color.AppDrawerBackground));
		}

		return convertView;
	}

	public void setSelected(int position) {
		this.selectedPosition = position;
		notifyDataSetChanged();
	}

	private int selectedPosition;
}