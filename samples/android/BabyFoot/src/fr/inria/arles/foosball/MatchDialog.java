package fr.inria.arles.foosball;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MatchDialog extends Dialog implements View.OnClickListener {

	public interface Handler {
		public void onMatchResult(int blueScore, int redScore);
	}

	private Handler handler;
	private String blueTeam;
	private String redTeam;

	public MatchDialog(Context context, Handler handler) {
		super(context, R.style.AppDialog);
		this.handler = handler;
	}

	public void setTeams(String blueTeam, String redTeam) {
		this.blueTeam = blueTeam;
		this.redTeam = redTeam;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.cancel:
			onCancel();
			break;
		case R.id.set:
			onSet();
			break;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dialog_match);
		setTitle(R.string.match_score_title);

		setCtrlText(R.id.blueTeam,
				getContext().getString(R.string.main_blue_team) + blueTeam);
		setCtrlText(R.id.redTeam, getContext()
				.getString(R.string.main_red_team) + redTeam);

		findViewById(R.id.cancel).setOnClickListener(this);
		findViewById(R.id.set).setOnClickListener(this);
	}

	protected void onCancel() {
		dismiss();
	}

	protected void onSet() {
		String blueScore = getCtrlText(R.id.blueTeamScore);
		String redScore = getCtrlText(R.id.redTeamScore);

		boolean error = false;
		try {
			int blue = Integer.parseInt(blueScore);
			int red = Integer.parseInt(redScore);

			if (blue + red >= 20 || (blue < 10 && red < 10) || blue > 10
					|| red > 10) {
				error = true;
			} else {
				handler.onMatchResult(blue, red);
				dismiss();
			}
		} catch (Exception ex) {
			error = true;
		}

		if (error) {
			Toast.makeText(getContext().getApplicationContext(), R.string.match_score_error,
					Toast.LENGTH_LONG).show();
		}
	}

	protected void setCtrlText(int resId, String text) {
		TextView txt = (TextView) findViewById(resId);
		if (txt != null) {
			txt.setText(text);
		}
	}

	protected String getCtrlText(int resId) {
		TextView txt = (TextView) findViewById(resId);
		if (txt != null) {
			return txt.getText().toString();
		}
		return null;
	}
}
