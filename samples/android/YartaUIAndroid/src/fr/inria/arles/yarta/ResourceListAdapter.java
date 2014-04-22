package fr.inria.arles.yarta;

import java.util.ArrayList;
import java.util.List;

import fr.inria.arles.yarta.basic.R;
import fr.inria.arles.yarta.core.YartaWrapper;
import fr.inria.arles.yarta.resources.Person;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ResourceListAdapter extends BaseAdapter implements
		View.OnClickListener {

	public ResourceListAdapter(Context context) {
		this.context = context;
		lstResources = new ArrayList<Object>();
		wrapper = YartaWrapper.getInstance();
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	class ViewHolder {
		ImageView icon;
		TextView title;
		TextView info;
		Button updateButton;
		Button messageButton;
		CheckBox checkbox;
		int position;
	}

	@Override
	public int getCount() {
		return lstResources.size();
	}

	@Override
	public Object getItem(int position) {
		return lstResources.get(position);
	}

	@Override
	public long getItemId(int position) {
		return lstResources.get(position).hashCode();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		Object currentObject = lstResources.get(position);

		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.resource_item, parent, false);
			holder = new ViewHolder();
			holder.icon = (ImageView) convertView.findViewById(R.id.itemIcon);
			holder.title = (TextView) convertView.findViewById(R.id.itemTitle);
			holder.info = (TextView) convertView.findViewById(R.id.itemInfo);
			holder.checkbox = (CheckBox) convertView
					.findViewById(R.id.itemCheck);
			holder.updateButton = (Button) convertView
					.findViewById(R.id.itemUpdate);
			holder.messageButton = (Button) convertView
					.findViewById(R.id.itemMessage);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (editable) {
			convertView.setOnClickListener(this);
		}
		
		holder.icon.setImageResource(wrapper.getResIcon(currentObject));
		holder.title.setText(wrapper.getResTitle(currentObject));
		holder.info.setText(wrapper.getResInfo(currentObject));
		holder.position = position;

		boolean isPerson = currentObject instanceof Person;

		if (isPerson && editable) {
			holder.messageButton.setVisibility(View.VISIBLE);
			holder.messageButton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					Person person = (Person) lstResources.get(position);
					MessageDialog dialog = new MessageDialog(context, person
							.getUserId());
					dialog.show();
				}
			});

			holder.updateButton.setVisibility(View.VISIBLE);
			holder.updateButton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					Person person = (Person) lstResources.get(position);
					boolean success = wrapper.sendUpdate(person.getUserId());

					Toast.makeText(
							context,
							success ? R.string.main_update_success
									: R.string.main_update_failed,
							Toast.LENGTH_SHORT).show();
				}
			});
		} else {
			holder.messageButton.setVisibility(View.GONE);
			holder.updateButton.setVisibility(View.GONE);
		}

		if (!editable) {
			holder.checkbox.setVisibility(View.VISIBLE);

			holder.checkbox.setChecked(lstChecks.get(position));
			holder.checkbox
					.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							lstChecks.set(position, isChecked);
						}
					});
		}

		return convertView;
	}

	@Override
	public void onClick(View v) {
		ViewHolder holder = (ViewHolder) v.getTag();
		wrapper.currentResource = lstResources.get(holder.position);
		wrapper.currentResourceClass = wrapper.currentResource.getClass();
		context.startActivity(new Intent(context, ResourceActivity.class));
	}

	public void setResources(List<Object> lstResources) {
		this.lstResources.clear();
		this.lstResources.addAll(lstResources);

		lstChecks = new ArrayList<Boolean>();
		for (int i = 0; i < lstResources.size(); i++) {
			lstChecks.add(false);
		}
		notifyDataSetChanged();
	}

	public boolean getChecked(int position) {
		return lstChecks.get(position);
	}

	private boolean editable = true;
	private YartaWrapper wrapper;
	private Context context;
	private List<Object> lstResources;
	private List<Boolean> lstChecks;
}
