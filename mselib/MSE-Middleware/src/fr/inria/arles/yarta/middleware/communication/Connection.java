package fr.inria.arles.yarta.middleware.communication;

public interface Connection {

	public abstract void setReceiver(Receiver receiver);

	public abstract void init(Object context);

	public abstract void uninit();

	public abstract int postMessage(String id, Message message);
}