package fr.inria.arles.yarta.desktop.library;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Service extends Remote {

	public static final String Name = Service.class.getName();

	public void start() throws RemoteException;

	public void stop() throws RemoteException;
	
	public void showUI() throws RemoteException;
}
