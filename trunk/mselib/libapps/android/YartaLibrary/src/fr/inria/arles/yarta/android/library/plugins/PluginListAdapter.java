package fr.inria.arles.yarta.android.library.plugins;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import fr.inria.arles.yarta.android.library.PluginLoginActivity;
import fr.inria.arles.yarta.R;
import fr.inria.arles.yarta.android.library.util.Settings;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Custom list adapter for displaying data extraction plug-ins.
 */
public class PluginListAdapter extends BaseAdapter {

	public PluginListAdapter(Context context, Settings settings) {
		this.context = context;
		pluginManager.init(settings);
		lstPlugins = pluginManager.getPlugins();
	}

	@Override
	public int getCount() {
		return lstPlugins.size();
	}

	@Override
	public Object getItem(int position) {
		return lstPlugins.get(position);
	}

	@Override
	public long getItemId(int position) {
		return getItem(position).hashCode();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View resultView = convertView;

		if (resultView == null) {
			resultView = LayoutInflater.from(context).inflate(
					R.layout.item_plugin, parent, false);
		}

		Plugin plugin = lstPlugins.get(position);

		TextView txtTitle = (TextView) resultView
				.findViewById(R.id.pluginTitle);
		txtTitle.setText(plugin.getNetworkName());

		Button btnAction = (Button) resultView.findViewById(R.id.pluginAction);
		TextView txtInfo = (TextView) resultView.findViewById(R.id.pluginInfo);

		if (plugin.isLoggedIn()) {
			String format = context
					.getString(R.string.dashboard_plugin_info_format);
			long lLastSync = plugin.getLastSync();

			String lastSync = context
					.getString(R.string.dashboard_plugin_sync_never);

			if (lLastSync > 0) {
				lastSync = dtfmt.format(new Date(lLastSync));
			}

			txtInfo.setText(String.format(format, plugin.getUserName(),
					lastSync));
			btnAction.setText(R.string.dashboard_logout);
			btnAction.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					Plugin plugin = lstPlugins.get(position);
					plugin.clearUserData();
					notifyDataSetChanged();
				}
			});
		} else {
			txtInfo.setText(R.string.dashboard_notloggedin);
			btnAction.setText(R.string.dashboard_login);
			btnAction.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (isInternetConnected()) {
						pluginManager.selectPlugin(position);
						context.startActivity(new Intent(context,
								PluginLoginActivity.class));
					} else {
						Toast.makeText(context, R.string.dashboard_nonet_error,
								Toast.LENGTH_SHORT).show();
					}
				}
			});
		}

		return resultView;
	}

	private boolean isInternetConnected() {
		try {
			ConnectivityManager connec = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo[] networksInfo = connec.getAllNetworkInfo();
			for (int i = 0; i < networksInfo.length; i++) {
				if (networksInfo[i].isConnected()) {
					return true;
				}
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private SimpleDateFormat dtfmt = new SimpleDateFormat("hh:mm, dd-mm-yy",
			Locale.US);
	private Context context;
	private PluginManager pluginManager = PluginManager.getInstance();
	private List<Plugin> lstPlugins;
}
