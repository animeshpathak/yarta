package fr.inria.arles.yarta;

import com.hp.hpl.jena.rdf.model.Model;

import fr.inria.arles.yarta.knowledgebase.MSEPolicyManager;
import fr.inria.arles.yarta.knowledgebase.interfaces.Graph;
import fr.inria.arles.yarta.knowledgebase.interfaces.Triple;

public class PolicyManagerProfiler extends MSEPolicyManager {

	private Profiler profiler;

	public PolicyManagerProfiler(Profiler profiler) {
		super();
		this.profiler = profiler;
	}

	@Override
	public boolean isAllowed(String arg0, String arg1, Model arg2, Graph arg3) {
		try {
			profiler.startMethod("PolicyManager.isAllowed_g");
			return super.isAllowed(arg0, arg1, arg2, arg3);
		} finally {
			profiler.stopMethod();
		}
	}

	@Override
	public boolean isAllowed(String arg0, String arg1, Model arg2, Triple arg3) {
		try {
			profiler.startMethod("PolicyManager.isAllowed_t");
			return super.isAllowed(arg0, arg1, arg2, arg3);
		} finally {
			profiler.stopMethod();
		}
	}

	@Override
	public Model getAllowedModel(String arg0, String arg1, Model arg2,
			Triple arg3) {
		try {
			profiler.startMethod("PolicyManager.getAllowedModel");
			return super.getAllowedModel(arg0, arg1, arg2, arg3);
		} finally {
			profiler.stopMethod();
		}
	}

	@Override
	public Model filteredModel(String arg0, String arg1, Model arg2, Graph arg3) {
		try {
			profiler.startMethod("PolicyManager.filteredModel");
			return super.filteredModel(arg0, arg1, arg2, arg3);
		} finally {
			profiler.stopMethod();
		}
	}
}
