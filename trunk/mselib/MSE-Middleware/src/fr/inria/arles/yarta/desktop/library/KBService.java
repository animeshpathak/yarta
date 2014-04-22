package fr.inria.arles.yarta.desktop.library;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import fr.inria.arles.yarta.Criteria;
import fr.inria.arles.yarta.knowledgebase.interfaces.Graph;
import fr.inria.arles.yarta.knowledgebase.interfaces.Node;
import fr.inria.arles.yarta.knowledgebase.interfaces.PolicyManager;
import fr.inria.arles.yarta.knowledgebase.interfaces.Triple;

public interface KBService extends Remote {

	public String getUserId() throws RemoteException;

	public Graph getPropertyObject(Node s, Node p, String requestorId)
			throws RemoteException;

	public Graph getPropertySubject(Node p, Node o, String requestorId)
			throws RemoteException;

	public Graph addGraph(Graph g, String requestorId) throws RemoteException;

	public Graph getProperty(Node s, Node o, String requestorId)
			throws RemoteException;

	public Graph getAllProperties(Node s, String requestorId)
			throws RemoteException;

	public Graph queryKB(Criteria c, String requestorId) throws RemoteException;

	public Graph getKB(String requestorId) throws RemoteException;

	public PolicyManager getPolicyManager() throws RemoteException;

	public void initialize(String source, String namespace, String policyFile,
			String userId) throws RemoteException;

	public void uninitialize() throws RemoteException;

	public Node addResource(String nodeURI, String typeURI, String requestorId)
			throws RemoteException;

	public Node addResource(Node node, String requestorId)
			throws RemoteException;

	public Node addLiteral(String value, String dataType, String requestorId)
			throws RemoteException;

	public Node getResourceByURI(String uri, String requestorId)
			throws RemoteException;

	public Node getResourceByURINoPolicies(String uri) throws RemoteException;

	public Triple addTriple(Node s, Node p, Node o, String requestorId)
			throws RemoteException;

	public Triple removeTriple(Node s, Node p, Node o, String requestorId)
			throws RemoteException;

	public Triple getTriple(Node s, Node p, Node o, String requestorId)
			throws RemoteException;

	public List<Triple> getPropertyObjectAsTriples(Node s, Node p,
			String requestorId) throws RemoteException;

	public List<Triple> getPropertySubjectAsTriples(Node p, Node o,
			String requestorId) throws RemoteException;

	public List<Triple> getPropertyAsTriples(Node s, Node o, String requestorId)
			throws RemoteException;

	public List<Triple> getAllPropertiesAsTriples(Node s, String requestorId)
			throws RemoteException;

	public String getMyNameSpace() throws RemoteException;

	public List<Triple> getKBAsTriples(String requestorId)
			throws RemoteException;
}
