package fr.inria.arles.yarta.middleware.communication;

public interface Receiver {

	public boolean handleMessage(String id, Message message);

	public Message handleRequest(String id, Message message);
}
