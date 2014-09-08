package fr.inria.arles.agent;

import javax.swing.JOptionPane;

import fr.inria.arles.yarta.middleware.communication.Message;
import fr.inria.arles.yarta.middleware.communication.Receiver;
import fr.inria.arles.yarta.middleware.msemanagement.MSEApplication;
import fr.inria.arles.yarta.middleware.msemanagement.MSEManager;

public class Main {

	private static MSEManager mse;

	public static void main(String args[]) {
		boolean quit = false;
		do {
			try {
				ServerApplication app = new ServerApplication();
				mse = new MSEManager();
				mse.setOwnerUID("inria");
				
				mse.initialize("res/mse-1.2.rdf", "res/policies", app, null);
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

