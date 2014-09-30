package fr.inria.arles.yarta.android.library.ui;

import java.util.ArrayList;
import java.util.List;

import fr.inria.arles.iris.R;
import fr.inria.arles.iris.web.ObjectItem;
import fr.inria.arles.yarta.android.library.util.ImageCache;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SearchListAdapter extends BaseAdapter {

	private List<ObjectItem> items = new ArrayList<ObjectItem>();

	private class ViewHolder {
		ImageView icon;
		TextView title;
		TextView info;
	}

	private LayoutInflater inflater;

	public SearchListAdapter(Context context) {
		this.inflater = LayoutInflater.from(context);
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_search, parent, false);

			holder = new ViewHolder();
			holder.icon = (ImageView) convertView.findViewById(R.id.icon);
			holder.title = (TextView) convertView.findViewById(R.id.author);
			holder.info = (TextView) convertView.findViewById(R.id.description);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		ObjectItem item = items.get(position);

		holder.title.setText(Html.fromHtml(item.getName()));

		holder.info.setVisibility(View.GONE);

		Bitmap bitmap = ImageCache.getBitmap(item);

		if (bitmap == null) {
			holder.icon.setImageResource(R.drawable.group_default);
		} else {
			holder.icon.setImageBitmap(bitmap);
		}

		return convertView;
	}

	public void setItems(List<ObjectItem> items) {
		this.items.clear();
		this.items.addAll(items);
		notifyDataSetChanged();
	}
}
