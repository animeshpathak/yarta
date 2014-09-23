package fr.inria.arles.yarta.desktop.library;

import java.rmi.Remote;
import java.rmi.RemoteException;

import fr.inria.arles.yarta.middleware.communication.CommunicationManager;
import fr.inria.arles.yarta.middleware.communication.Message;

/**
 * RMI Communication Manager Wrapper. See {@link CommunicationManager}
 */
public interface CMService extends Remote {

	int clear() throws RemoteException;

	int sendUpdateRequest(String peerId) throws RemoteException;

	int sendHello(String peerId) throws RemoteException;

	int sendNotify(String peerId) throws RemoteException;

	int sendMessage(String peerId, Message message) throws RemoteException;

	boolean registerReceiver(Receiver receiver) throws RemoteException;

	boolean unregisterReceiver(Receiver receiver) throws RemoteException;

	void registerApplication(Application application) throws RemoteException;

	void unregisterApplication(Application application) throws RemoteException;
}
