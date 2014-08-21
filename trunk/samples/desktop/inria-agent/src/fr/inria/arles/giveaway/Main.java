package fr.inria.arles.giveaway;

import javax.swing.JOptionPane;

import fr.inria.arles.giveaway.msemanagement.MSEManagerEx;
import fr.inria.arles.yarta.middleware.communication.Message;
import fr.inria.arles.yarta.middleware.communication.Receiver;
import fr.inria.arles.yarta.middleware.msemanagement.MSEApplication;

public class Main {

	private static MSEManagerEx mse;

	public static void main(String args[]) {
		boolean quit = false;
		do {
			try {
				ServerApplication app = new ServerApplication();
				mse = new MSEManagerEx();
				mse.setOwnerUID("inria@inria.fr");
				
				// make sure we understand babyfoot as well
				mse.initialize("res/foosball.rdf", "res/policies", app, null);
				mse.shutDown();
				
				mse.initialize("res/donations.rdf", "res/policies", app, null);
				mse.getCommunicationManager().setMessageReceiver(app);

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
