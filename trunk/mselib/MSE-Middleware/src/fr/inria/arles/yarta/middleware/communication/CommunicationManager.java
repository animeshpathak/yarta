package fr.inria.arles.yarta.middleware.communication;

import fr.inria.arles.yarta.knowledgebase.KBException;
import fr.inria.arles.yarta.knowledgebase.interfaces.KnowledgeBase;
import fr.inria.arles.yarta.middleware.msemanagement.MSEApplication;

/**
 * The communication manager interface. It is being used by the Yarta middleware
 * to communicate with other Yarta middleware application end-points.
 */
public interface CommunicationManager {

	/**
	 * Initializes the communication manager
	 * 
	 * @param userID
	 * @param knowledgeBase
	 * @param application
	 * @param context
	 * @return int
	 */
	public int initialize(String userID, KnowledgeBase knowledgeBase,
			MSEApplication application, Object context);

	/**
	 * Does all the un-initialization.
	 * 
	 * @return int
	 */
	public int uninitialize();

	/**
	 * Clears the current user token and resets the communication;
	 * 
	 * @return int
	 */
	public int clear();

	/**
	 * Sets a receiver to receive messages.
	 * 
	 * @param receiver
	 */
	public void setMessageReceiver(Receiver receiver);

	/**
	 * Sends a HELLO request to a remote peer.
	 * 
	 * @param partnerID
	 *            the peer id
	 * @return int
	 * 
	 * @throws KBException
	 */
	public int sendHello(String partnerID) throws KBException;

	/**
	 * Sends an UPDATE request to a remote peer.
	 * 
	 * @param partnerID
	 *            the peer id
	 * @return int
	 * 
	 * @throws KBException
	 */
	public int sendUpdateRequest(String partnerID) throws KBException;

	/**
	 * Sends a message to a remote peer.
	 * 
	 * @param peerId
	 * @param message
	 * 
	 * @return int
	 */
	public int sendMessage(String peerId, Message message);

	/**
	 * Sends a Notify message to force an update.
	 * 
	 * @param peerId
	 * @return
	 */
	public int sendNotify(String peerId);
}
