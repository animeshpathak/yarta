package fr.inria.arles.yarta.middleware.communication;

public class NotifyMessage extends Message {

	private static final long serialVersionUID = 1L;

	public NotifyMessage() {
		super(Message.TYPE_NOTIFY, new byte[0]);
	}
}
