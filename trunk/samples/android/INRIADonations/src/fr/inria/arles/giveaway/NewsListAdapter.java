package fr.inria.arles.giveaway;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import fr.inria.arles.giveaway.resources.Announcement;
import fr.inria.arles.giveaway.resources.Donation;
import fr.inria.arles.giveaway.resources.Picture;
import fr.inria.arles.giveaway.resources.Request;
import fr.inria.arles.giveaway.resources.Sale;
import fr.inria.arles.giveaway.util.FastCache;
import fr.inria.arles.yarta.resources.Resource;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NewsListAdapter extends BaseAdapter implements
		View.OnClickListener {

	private ContentClientPictures client;

	public interface Handler {
		public void onClick(View view, int position);
	}

	private class ViewHolder {
		TextView title;
		TextView content;
		TextView category;
		TextView time;
		ImageView image;
		ImageView menu;
	}

	private Context context;

	private FastCache cache;

	private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm, dd/MM",
			Locale.US);

	private Handler handler;

	private String priceFormat;

	public NewsListAdapter(Context context, Handler handler) {
		this.context = context;
		this.handler = handler;
		this.cache = new FastCache(context);
		this.client = new ContentClientPictures(context);
		this.priceFormat = context.getString(R.string.price_format);
	}

	@Override
	public int getCount() {
		return anouncements.size();
	}

	@Override
	public Object getItem(int position) {
		return anouncements.get(position);
	}

	@Override
	public long getItemId(int position) {
		return getItem(position).hashCode();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {

			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_news, parent, false);

			holder = new ViewHolder();

			holder.title = (TextView) convertView.findViewById(R.id.title);
			holder.content = (TextView) convertView.findViewById(R.id.content);
			holder.category = (TextView) convertView
					.findViewById(R.id.category);
			holder.time = (TextView) convertView.findViewById(R.id.time);
			holder.menu = (ImageView) convertView.findViewById(R.id.menu);
			holder.image = (ImageView) convertView.findViewById(R.id.icon);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		if (position == 0) {
			convertView.findViewById(R.id.top).setVisibility(View.INVISIBLE);
		}
		if (position == anouncements.size() - 1) {
			convertView.findViewById(R.id.bottom).setVisibility(View.INVISIBLE);
		}
		if (position > 0) {
			convertView.findViewById(R.id.top).setVisibility(View.VISIBLE);
		}
		if (position < anouncements.size() - 1) {
			convertView.findViewById(R.id.bottom).setVisibility(View.VISIBLE);
		}

		Announcement ad = (Announcement) getItem(position);

		holder.title.setText(ad.getTitle());
		long time = 0;
		try {
			time = ad.getTime();
		} catch (Exception ex) {
		}
		holder.time.setText(sdf.format(new Date(time)));
		holder.category.setText(Common.getLocalizedCategory(context, ad));

		holder.menu.setTag(position);
		holder.menu.setOnClickListener(this);

		int price = 0;
		try {
			float fPrice = ad.getPrice();
			price = (int) fPrice;
		} catch (Exception ex) {
		}

		String text = null;
		if (price > 0) {
			text = String.format(priceFormat, price);
			holder.content.setTextColor(Color.rgb(0xf4, 0x84, 0x2d));
		} else {
			text = context.getString(R.string.price_free);
			holder.content.setTextColor(Color.rgb(0x96, 0xaa, 0x39));
		}
		holder.content.setText(text);

		if (ad instanceof Donation || ad instanceof Sale) {
			Bitmap bitmap = null;
			for (Picture p : ad.getPicture()) {
				bitmap = cache.getBitmap(getShortName(p));
				if (bitmap == null) {
					bitmap = client.getSmallBitmap(getShortName(p));
					cache.setBitmap(getShortName(p), bitmap);
				}
				break;
			}
			holder.image.setVisibility(View.VISIBLE);
			if (bitmap != null) {
				holder.image.setImageBitmap(bitmap);
			} else {
				holder.image.setImageResource(R.drawable.image_default);
			}
		} else if (ad instanceof Request) {
			holder.image.setImageResource(R.drawable.image_request);
		}

		return convertView;
	}

	private String getShortName(Resource resource) {
		String uniqueId = resource.getUniqueId();
		return uniqueId.substring(uniqueId.indexOf('#') + 1);
	}

	public void setItems(List<Announcement> anouncements) {
		this.anouncements.clear();
		this.anouncements.addAll(anouncements);
		notifyDataSetChanged();
	}

	List<Announcement> anouncements = new ArrayList<Announcement>();

	@Override
	public void onClick(View v) {
		int position = (Integer) v.getTag();
		handler.onClick(v, position);
	}
}
