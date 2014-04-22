package fr.inria.arles.yarta.desktop.library;

import java.io.File;
import java.rmi.RemoteException;
import java.util.List;

import fr.inria.arles.yarta.Criteria;
import fr.inria.arles.yarta.knowledgebase.KBException;
import fr.inria.arles.yarta.knowledgebase.interfaces.Graph;
import fr.inria.arles.yarta.knowledgebase.interfaces.KnowledgeBase;
import fr.inria.arles.yarta.knowledgebase.interfaces.Node;
import fr.inria.arles.yarta.knowledgebase.interfaces.PolicyManager;
import fr.inria.arles.yarta.knowledgebase.interfaces.Triple;
import fr.inria.arles.yarta.middleware.msemanagement.MSEApplication;

public class KBClient implements KnowledgeBase {

	private KBService service;

	private MSEApplication app;

	public KBClient(MSEApplication app) {
		this.app = app;
	}

	@Override
	public void initialize(String source, String namespace, String policyFile,
			String userId) throws KBException {
		DependencyCheck.ensureYartaIsInstalledAndRunning();

		service = RMIUtil.getObject(Service.Name);
		if (service == null) {
			throw new KBException(
					"Yarta Library Service is not started or not installed.");
		}
		try {
			service.initialize(new File(source).getAbsolutePath(), namespace,
					new File(policyFile).getAbsolutePath(), userId);
		} catch (RemoteException ex) {
			Throwable t = ex.getCause();
			if (t instanceof KBException) {
				throw (KBException) t;
			} else {
				ex.printStackTrace();
			}
		}

		try {
			app.handleKBReady(service.getUserId());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void uninitialize() throws KBException {
		try {
			service.uninitialize();
		} catch (RemoteException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public Node addResource(String nodeURI, String typeURI, String requestorId)
			throws KBException {
		try {
			return service.addResource(nodeURI, typeURI, requestorId);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	@Override
	public Node addResource(Node node, String requestorId) throws KBException {
		try {
			service.addResource(node, requestorId);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	@Override
	public Node addLiteral(String value, String dataType, String requestorId)
			throws KBException {
		try {
			return service.addLiteral(value, dataType, requestorId);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	@Override
	public Node getResourceByURI(String uri, String requestorId)
			throws KBException {
		try {
			return service.getResourceByURI(uri, requestorId);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	@Override
	public Node getResourceByURINoPolicies(String uri) {
		try {
			return service.getResourceByURINoPolicies(uri);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	@Override
	public Triple addTriple(Node s, Node p, Node o, String requestorId)
			throws KBException {
		try {
			return service.addTriple(s, p, o, requestorId);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	@Override
	public Triple removeTriple(Node s, Node p, Node o, String requestorId)
			throws KBException {
		try {
			return service.removeTriple(s, p, o, requestorId);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	@Override
	public Triple getTriple(Node s, Node p, Node o, String requestorId)
			throws KBException {
		try {
			return service.getTriple(s, p, o, requestorId);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	@Override
	public List<Triple> getPropertyObjectAsTriples(Node s, Node p,
			String requestorId) throws KBException {
		try {
			return service.getPropertyObjectAsTriples(s, p, requestorId);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	@Override
	public List<Triple> getPropertySubjectAsTriples(Node p, Node o,
			String requestorId) throws KBException {
		try {
			return service.getPropertySubjectAsTriples(p, o, requestorId);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	@Override
	public List<Triple> getPropertyAsTriples(Node s, Node o, String requestorId)
			throws KBException {
		try {
			return service.getPropertyAsTriples(s, o, requestorId);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	@Override
	public List<Triple> getAllPropertiesAsTriples(Node s, String requestorId)
			throws KBException {
		try {
			return service.getAllPropertiesAsTriples(s, requestorId);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	@Override
	public List<Triple> getKBAsTriples(String requestorId) throws KBException {
		try {
			return service.getKBAsTriples(requestorId);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	@Override
	public Graph getPropertyObject(Node s, Node p, String requestorId)
			throws KBException {
		try {
			return service.getPropertyObject(s, p, requestorId);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	@Override
	public Graph getPropertySubject(Node p, Node o, String requestorId)
			throws KBException {
		try {
			return service.getPropertySubject(p, o, requestorId);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	@Override
	public Graph addGraph(Graph g, String requestorId) throws Exception {
		try {
			return service.addGraph(g, requestorId);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	@Override
	public Graph getProperty(Node s, Node o, String requestorId)
			throws KBException {
		try {
			return service.getProperty(s, o, requestorId);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	@Override
	public Graph getAllProperties(Node s, String requestorId)
			throws KBException {
		try {
			return service.getAllProperties(s, requestorId);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	@Override
	public Graph queryKB(Criteria c, String requestorId) throws KBException {
		try {
			return service.queryKB(c, requestorId);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	@Override
	public String getMyNameSpace() {
		try {
			return service.getMyNameSpace();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	@Override
	public Graph getKB(String requestorId) throws KBException {
		try {
			return service.getKB(requestorId);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	@Override
	public PolicyManager getPolicyManager() {
		try {
			return service.getPolicyManager();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

}
