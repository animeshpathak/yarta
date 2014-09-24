package fr.inria.arles.yarta.android.library.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import fr.inria.arles.iris.R;
import fr.inria.arles.yarta.android.library.ContentClientPictures;
import fr.inria.arles.yarta.android.library.resources.Person;
import fr.inria.arles.yarta.android.library.resources.PersonImpl;
import fr.inria.arles.yarta.android.library.resources.Picture;
import fr.inria.arles.yarta.resources.Agent;
import fr.inria.arles.yarta.resources.Content;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ContentListAdapter extends BaseAdapter implements
		View.OnClickListener {

	public interface Callback {
		public void onClickProfile(Person item);

		public void onClickPost(Content item);
	}

	private class ViewHolder {
		public TextView author;
		public TextView content;
		public TextView time;
		public ImageView image;
		public View container;
		public View bottomSeparator;
	}

	private List<Content> items = new ArrayList<Content>();
	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM, HH:mm",
			Locale.getDefault());

	private LayoutInflater inflater;
	private Callback callback;
	private ContentClientPictures content;

	public ContentListAdapter(Context context) {
		this.content = new ContentClientPictures(context);
		inflater = LayoutInflater.from(context);
	}

	public void setCallback(Callback callback) {
		this.callback = callback;
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
		Content item = items.get(position);

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_post, parent, false);

			holder = new ViewHolder();
			holder.author = (TextView) convertView.findViewById(R.id.author);
			holder.content = (TextView) convertView.findViewById(R.id.content);
			holder.time = (TextView) convertView.findViewById(R.id.time);
			holder.container = convertView.findViewById(R.id.container);
			holder.image = (ImageView) convertView.findViewById(R.id.icon);
			holder.bottomSeparator = convertView
					.findViewById(R.id.bottom_separator);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Person person = getAuthor(item);

		holder.author.setText(Html.fromHtml(person.getName()));
		holder.author.setTag(position);
		holder.author.setOnClickListener(this);

		holder.container.setTag(position);
		holder.container.setOnClickListener(this);

		if (item.getTitle() != null) {
			holder.content.setText(Html.fromHtml(item.getTitle()));
		}
		holder.time.setText(sdf.format(new Date(item.getTime())));

		Bitmap bitmap = null;

		for (Picture picture : person.getPicture()) {
			bitmap = content.getBitmap(picture);
		}

		if (bitmap != null) {
			holder.image.setImageBitmap(bitmap);
		}

		boolean isLast = position == getCount() - 1;
		holder.bottomSeparator.setVisibility(isLast ? View.VISIBLE : View.GONE);

		return convertView;
	}

	public void setItems(List<Content> items) {
		this.items.clear();
		this.items.addAll(items);
		notifyDataSetChanged();
	}

	private Person getAuthor(Content content) {
		Person person = null;
		for (Agent agent : content.getCreator_inverse()) {
			person = (PersonImpl) agent;
		}
		return person;
	}

	@Override
	public void onClick(View v) {
		int position = (Integer) v.getTag();
		Content item = items.get(position);

		switch (v.getId()) {
		case R.id.container:
			callback.onClickPost(item);
			break;
		case R.id.author:
			callback.onClickProfile(getAuthor(item));
			break;
		}
	}
}
