package fr.inria.arles.yarta.android.library;

import fr.inria.arles.yarta.android.library.IMSEApplication;
import fr.inria.arles.yarta.android.library.IPolicyManager;
import fr.inria.arles.yarta.android.library.IReceiver;

interface ILibraryService {

	String getUserId();
	boolean clear();
	 
	/*** KnowledgeBase related functions ***/
	void initialize(in IMSEApplication app, in String source, in String namespace, in String policyFile, in String userId);
	void uninitialize();

	Bundle addLiteral(in String value, in String dataType, in String requestorId);
	Bundle addResource(in String nodeURI, in String typeURI, in String requestorId);
	Bundle addResourceNode(in Bundle node, in String requestorId);

	String getMyNameSpace();

	Bundle addTriple(in Bundle s, in Bundle p, in Bundle o, in String requestorId);
	Bundle getTriple(in Bundle s, in Bundle p, in Bundle o, in String requestorId);
	Bundle removeTriple(in Bundle s, in Bundle p, in Bundle o, in String requestorId);

	Bundle getResourceByURI(in String URI, in String requestorId);
	Bundle getResourceByURINoPolicies(in String URI);

	List<Bundle> getPropertyObjectAsTriples(in Bundle s, in Bundle p,
			in String requestorId);
	List<Bundle> getPropertySubjectAsTriples(in Bundle p, in Bundle o,
			in String requestorId);
	List<Bundle> getPropertyAsTriples(in Bundle s, in Bundle o, in String requestorId);
	List<Bundle> getAllPropertiesAsTriples(in Bundle s, in String requestorId);
	List<Bundle> getKBAsTriples(in String requestorId);
	
	Bundle getPropertyObject(in Bundle s, in Bundle p, in String requestorId);
	Bundle getPropertySubject(in Bundle p, in Bundle o, in String requestorId);
	Bundle addGraph(in Bundle g, in String requestorId);
	Bundle getProperty(in Bundle s, in Bundle o, in String requestorId);
	Bundle getAllProperties(in Bundle s, in String requestorId);
	Bundle queryKB(in Bundle c, in String requestorId);
	Bundle getKB(in String requestorId);
	IPolicyManager getPolicyManager();
	
	/*** CommunicationManager related functions ***/
	int sendUpdateRequest(String peerId);
	int sendHello(String peerId);
	int sendNotify(String peerId);
	int sendResource(String peerId, String resId);
	int sendMessage(in String peerId, in Bundle message);
	boolean registerReceiver(IReceiver receiver);
	boolean unregisterReceiver(IReceiver receiver);
	
	/*** MSEApplication binding related functions ***/
	boolean registerCallback(IMSEApplication callback);
	boolean unregisterCallback(IMSEApplication callback);
}