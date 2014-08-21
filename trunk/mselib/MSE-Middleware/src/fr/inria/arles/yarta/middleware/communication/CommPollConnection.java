package fr.inria.arles.yarta.middleware.communication;

public class CommPollConnection implements Connection {

	public static final String JavaClient = "j2se";
	public static final int ServerCheckTimeout = 15 * 1000;

	public CommPollConnection(String userId) {
		this.userId = userId;
	}

	@Override
	public void setReceiver(Receiver receiver) {
		this.receiver = receiver;
	}

	@Override
	public void init(Object context) {
		CommClient.init(userId, JavaClient);
		CommClient.addCallback(receiver);

		checkMessagesTask.start();
	}

	@Override
	public void uninit() {
		CommClient.removeCallback(receiver);
		CommClient.uninit(userId, JavaClient);

		try {
			checkMessagesTask.interrupt();
			checkMessagesTask.join();
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public int postMessage(String id, Message message) {
		return CommClient.post(id, message);
	}

	private Receiver receiver;
	private String userId;

	private boolean stop;
	private Thread checkMessagesTask = new Thread(new Runnable() {

		@Override
		public void run() {
			while (!stop) {
				try {
					while (CommClient.get(userId) != null) {
						Thread.sleep(100);
					}
					Thread.sleep(ServerCheckTimeout);
				} catch (InterruptedException ex) {
					stop = true;
				}
			}
		}
	});
}
