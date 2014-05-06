package fr.inria.arles.foosball;

import java.util.List;

import fr.inria.arles.foosball.resources.Player;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class SelectPlayerDialog extends BaseDialog implements
		AdapterView.OnItemClickListener {

	private SimplePlayerListAdapter adapter;
	private List<Player> players;
	private PlayerSelector selector;

	public interface PlayerSelector {
		public void onPlayerSelected(Player player);
	}

	public static void show(Context context, List<Player> players,
			PlayerSelector selector) {
		SelectPlayerDialog dialog = new SelectPlayerDialog(context);
		dialog.setPlayers(players);
		dialog.setSelector(selector);
		dialog.show();
	}

	public SelectPlayerDialog(Context context) {
		super(context);
	}

	public void setPlayers(List<Player> players) {
		this.players = players;
	}

	public void setSelector(PlayerSelector selector) {
		this.selector = selector;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dialog_select_player);
		setTitle(R.string.match_select_player);

		adapter = new SimplePlayerListAdapter(getContext());
		adapter.setItems(players);

		ListView list = (ListView) findViewById(R.id.listPlayers);
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (selector != null) {
			selector.onPlayerSelected((Player) adapter.getItem(position));
		}
		dismiss();
	}
}
