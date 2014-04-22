package fr.inria.arles.yarta.desktop.library;

import java.rmi.Remote;
import java.rmi.RemoteException;

import fr.inria.arles.yarta.middleware.communication.Message;

public interface Receiver extends Remote {

	public static final String Name = Receiver.class.getName();

	public void handleMessage(String id, Message message)
			throws RemoteException;

	public Message handleRequest(String id, Message message)
			throws RemoteException;

	public String getAppId() throws RemoteException;
}
