package fr.inria.arles.yarta.desktop.library;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Application extends Remote {

	public static final String Name = Application.class.getName();

	public void handleNotification(String notification) throws RemoteException;

	public boolean handleQuery(String query) throws RemoteException;

	public void handleKBReady(String userId) throws RemoteException;
}
