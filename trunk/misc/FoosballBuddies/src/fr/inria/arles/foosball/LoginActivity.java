package fr.inria.arles.foosball;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.facebook.LoggingBehavior;
import com.facebook.Request;
import com.facebook.Request.GraphUserCallback;
import com.facebook.Request.GraphUserListCallback;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.model.GraphUser;

import fr.inria.arles.foosball.util.FoosballCore;
import fr.inria.arles.foosball.util.Player;

public class LoginActivity extends BaseActivity {

	private Session.StatusCallback statusCallback = new SessionStatusCallback();

	private class SessionStatusCallback implements Session.StatusCallback {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			if (exception != null) {
				exception.printStackTrace();
			}
			updateView();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);

		Session session = Session.getActiveSession();
		if (session == null) {
			if (savedInstanceState != null) {
				session = Session.restoreSession(this, null, statusCallback,
						savedInstanceState);
			}
			if (session == null) {
				session = new Session(this);
			}

			if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
				session.openForRead(new Session.OpenRequest(this)
						.setCallback(statusCallback));
			}
		}

		performLogin();
	}

	@Override
	protected void onStart() {
		super.onStart();

		Session session = Session.getActiveSession();
		if (session != null)
			session.addCallback(statusCallback);
	}

	@Override
	protected void onStop() {
		Session session = Session.getActiveSession();
		if (session != null)
			session.removeCallback(statusCallback);
		super.onStop();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Session session = Session.getActiveSession();
		Session.saveSession(session, outState);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode,
				resultCode, data);
	}

	protected void performLogin() {
		Session session = Session.getActiveSession();

		if (session == null) {
			session = Session.openActiveSession(this, true, statusCallback);
		} else if (!session.isOpened() && !session.isClosed()) {
			session = Session.openActiveSession(this, true, statusCallback);
		} else {
			session = Session.openActiveSession(this, true, statusCallback);
		}
	}

	protected boolean finish;

	protected void continueToApp() {
		startActivity(new Intent(this, MainActivity.class));
		finish();
	}

	protected void updateView() {
		final Session session = Session.getActiveSession();
		if (session == null) {
			return;
		}
		if (session.isOpened()) {

			finish = false;
			Request meRequest = Request.newMeRequest(session,
					new GraphUserCallback() {

						@Override
						public void onCompleted(GraphUser user,
								Response response) {
							core.setCurrentUser(user.getId(), user.getName());

							Request friendRequest = Request
									.newMyFriendsRequest(session,
											new GraphUserListCallback() {
												@Override
												public void onCompleted(
														List<GraphUser> users,
														Response response) {

													List<Player> players = new ArrayList<Player>();

													for (GraphUser user : users) {
														players.add(new Player(
																user.getId(),
																user.getName()));
													}

													core.setFriends(players);

													continueToApp();
												}
											});
							Bundle params = new Bundle();
							params.putString("fields", "id,name");

							friendRequest.setParameters(params);
							friendRequest.executeAsync();


						}
					});
			Bundle params = new Bundle();
			params.putString("fields", "id,name");
			
			meRequest.setParameters(params);
			meRequest.executeAsync();
		}
	}

	private FoosballCore core = FoosballCore.getInstance();
}
