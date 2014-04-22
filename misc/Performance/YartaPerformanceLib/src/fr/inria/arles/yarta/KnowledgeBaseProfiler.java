package fr.inria.arles.yarta;

import java.util.List;

import fr.inria.arles.yarta.knowledgebase.AccessControlException;
import fr.inria.arles.yarta.knowledgebase.KBException;
import fr.inria.arles.yarta.knowledgebase.MSEKnowledgeBase;
import fr.inria.arles.yarta.knowledgebase.interfaces.Node;
import fr.inria.arles.yarta.knowledgebase.interfaces.Triple;

public class KnowledgeBaseProfiler extends MSEKnowledgeBase {

	public KnowledgeBaseProfiler(Profiler profiler) {
		super();

		// hook the policy manager also
		policyManager = new PolicyManagerProfiler(profiler);

		this.profiler = profiler;
	}

	@Override
	public void initialize(String arg0, String arg1, String arg2, String arg3)
			throws KBException {
		try {
			profiler.startMethod("KnowledgeBase.initialize");
			super.initialize(arg0, arg1, arg2, arg3);
		} finally {
			profiler.stopMethod();
		}
	}

	@Override
	public void uninitialize() throws KBException {
		try {
			profiler.startMethod("KnowledgeBase.uninitialize");
			super.uninitialize();
		} finally {
			profiler.stopMethod();
		}
	}

	@Override
	public Triple addTriple(Node arg0, Node arg1, Node arg2, String arg3)
			throws KBException {
		try {
			profiler.startMethod("KnowledgeBase.addTriple");
			return super.addTriple(arg0, arg1, arg2, arg3);
		} finally {
			profiler.stopMethod();
		}
	}

	@Override
	public Triple removeTriple(Node arg0, Node arg1, Node arg2, String arg3)
			throws KBException {
		try {
			profiler.startMethod("KnowledgeBase.removeTriple");
			return super.removeTriple(arg0, arg1, arg2, arg3);
		} finally {
			profiler.stopMethod();
		}
	}

	@Override
	public List<Triple> getPropertyObjectAsTriples(Node s, Node p,
			String requestorId) throws KBException {
		try {
			profiler.startMethod("KnowledgeBase.getPropertyObjectAsTriples");
			return super.getPropertyObjectAsTriples(s, p, requestorId);
		} finally {
			profiler.stopMethod();
		}
	}

	@Override
	public Node getResourceByURI(String arg0, String arg1)
			throws AccessControlException {
		try {
			profiler.startMethod("KnowledgeBase.getResourceByURI");
			return super.getResourceByURI(arg0, arg1);
		} finally {
			profiler.stopMethod();
		}
	}

	private Profiler profiler;
}
