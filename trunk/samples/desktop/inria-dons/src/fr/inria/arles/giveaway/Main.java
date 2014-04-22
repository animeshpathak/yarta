package fr.inria.arles.giveaway;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;

import bitoflife.chatterbean.AliceBot;
import bitoflife.chatterbean.ChatterBeanException;
import bitoflife.chatterbean.parser.AliceBotParser;
import bitoflife.chatterbean.util.Searcher;

import fr.inria.arles.giveaway.msemanagement.MSEManagerEx;
import fr.inria.arles.giveaway.resources.PersonImpl;
import fr.inria.arles.yarta.middleware.communication.Message;
import fr.inria.arles.yarta.middleware.communication.Receiver;
import fr.inria.arles.yarta.middleware.msemanagement.MSEApplication;
import fr.inria.arles.yarta.resources.Agent;
import fr.inria.arles.yarta.resources.Conversation;
import fr.inria.arles.yarta.resources.Person;
import fr.inria.arles.yarta.resources.YartaResource;

public class Main {

	private static MSEManagerEx mse;
	private static AliceBot alice;

	private static List<fr.inria.arles.yarta.resources.Message> answeredMessages = new ArrayList<fr.inria.arles.yarta.resources.Message>();

	public static void main(String args[]) {

		alice = new AliceBot();

		boolean quit = false;
		do {

			try {
				ServerApplication app = new ServerApplication();
				mse = new MSEManagerEx();
				mse.initialize("res/donations.rdf", "res/policies", app, null);
				mse.getCommunicationManager().setMessageReceiver(app);

				Set<fr.inria.arles.yarta.resources.Message> ms = mse
						.getStorageAccessManager().getAllMessages();
				answeredMessages.addAll(ms);

				AliceBotParser parser = new AliceBotParser();
				parser.parse(alice, openStream("aiml/context.xml"),
						openStream("aiml/splitters.xml"),
						openStream("aiml/substitutions.xml"),
						openStreams("aiml/Net"));

				int result = -1;

				try {
					result = JOptionPane.showConfirmDialog(null,
							"DnsIServ - Running. Restart?", "YAgent",
							JOptionPane.OK_CANCEL_OPTION);

					if (result == JOptionPane.CANCEL_OPTION) {
						quit = true;
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					System.out.println("No UI. sleeping and then wake up...");
					Thread.sleep(30 * 60 * 1000);
				}

				mse.shutDown();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} while (!quit);
	}

	private static InputStream openStream(String name) {
		InputStream is = null;
		try {
			is = new FileInputStream(name);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return is;
	}

	private static InputStream[] openStreams(String name) {
		return searchAIMLStreams(name);
	}

	private static InputStream[] searchAIMLStreams(String path) {
		try {
			Searcher searcher = new Searcher();
			return searcher.search(new File(".").toURI().toURL(), path,
					".+\\.aiml");
		} catch (Exception e) {
			throw new ChatterBeanException(e);
		}
	}

	private static class ServerApplication implements MSEApplication, Receiver {

		private void log(String format, Object... args) {
			System.out.println(String.format(format, args));
		}

		@Override
		public boolean handleMessage(String id, Message message) {
			log("handleMessage(%s, %d)", id, message.getType());
			if (message.getType() == Message.TYPE_HELLO) {
				try {
					mse.getCommunicationManager().sendUpdateRequest(id);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			return false;
		}

		@Override
		public Message handleRequest(String id, Message message) {
			log("handleRequest(%s, %d)", id, message.getType());
			return null;
		}

		@Override
		public void handleNotification(String notification) {
			log("handleNotification(%s)", notification);

			try {
				// notify all peers
				Person me = mse.getStorageAccessManagerEx().getMe();

				Set<Conversation> conversations = me.getParticipatesTo();

				for (Conversation c : conversations) {
					for (fr.inria.arles.yarta.resources.Message m : c
							.getMessages()) {
						if (answeredMessages.indexOf(m) == -1
								&& !m.getCreator_inverse().contains(me)) {
							answeredMessages.add(m);

							String title = m.getTitle();
							String response = alice.respond(title);
							fr.inria.arles.yarta.resources.Message msg = mse
									.getStorageAccessManager().createMessage();

							me.addCreator(msg);
							msg.setTime(System.currentTimeMillis());
							msg.setTitle(response);
							c.addMessage(msg);

							answeredMessages.add(msg);

							for (Agent a : c.getParticipatesTo_inverse()) {
								if (!a.equals(me)) {
									Person p = new PersonImpl(
											mse.getStorageAccessManager(),
											((YartaResource) a).getNode());
									mse.getCommunicationManager().sendNotify(
											p.getUserId());
								}
							}
						}
					}
				}

				Set<Agent> agents = me.getKnows();

				for (Agent a : agents) {
					Person p = new PersonImpl(mse.getStorageAccessManagerEx(),
							((YartaResource) a).getNode());
					mse.getCommunicationManager().sendNotify(p.getUserId());
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		@Override
		public boolean handleQuery(String query) {
			log("handleQuery(%s)", query);
			return true;
		}

		@Override
		public void handleKBReady(String userId) {
			log("handleKBReady(%s)", userId);
			mse.setOwnerUID(userId);
		}

		@Override
		public String getAppId() {
			return "fr.inria.arles.giveaway";
		}
	}
}
