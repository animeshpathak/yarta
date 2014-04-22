package fr.inria.arles.yarta.policy;

import java.util.List;

import fr.inria.arles.yarta.conference.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PoliciesListAdapter extends BaseAdapter {

	class ViewHolder {
		TextView title;
		TextView info;
		View remove;
	}

	public PoliciesListAdapter(Context context) {
		this.context = context;
		s_instance = this;
	}

	public static PoliciesListAdapter getInstance() {
		return s_instance;
	}

	@Override
	public int getCount() {
		return lstRules.size();
	}

	@Override
	public Object getItem(int position) {
		return lstRules.get(position);
	}

	@Override
	public long getItemId(int position) {
		return getItem(position).hashCode();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		AccessRule rule = lstRules.get(position);

		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.rule_item, parent, false);

			holder = new ViewHolder();
			holder.title = (TextView) convertView.findViewById(R.id.ruleTitle);
			holder.info = (TextView) convertView.findViewById(R.id.ruleInfo);
			holder.remove = convertView.findViewById(R.id.deleteButton);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.title.setText(String.format(
				context.getString(R.string.rule_title), position));
		holder.info.setText(String.format(
				context.getString(R.string.rule_description),
				rule.toShortString()));

		if (holder.remove != null) {
			holder.remove.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					lstRules.remove(position);
					notifyDataSetChanged();
				}
			});
		}
		return convertView;
	}

	public void setRules(List<AccessRule> lstRules) {
		this.lstRules = lstRules;
	}

	public List<AccessRule> getRules() {
		return lstRules;
	}

	public void addRule(AccessRule rule) {
		lstRules.add(rule);
	}

	private Context context;
	private List<AccessRule> lstRules;
	private static PoliciesListAdapter s_instance;
}
