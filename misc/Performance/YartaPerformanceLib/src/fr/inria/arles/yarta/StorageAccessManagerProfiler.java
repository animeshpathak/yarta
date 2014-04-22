package fr.inria.arles.yarta;

import java.util.Set;

import fr.inria.arles.yarta.knowledgebase.KBException;
import fr.inria.arles.yarta.knowledgebase.interfaces.Node;
import fr.inria.arles.yarta.middleware.msemanagement.StorageAccessManager;
import fr.inria.arles.yarta.resources.Person;

public class StorageAccessManagerProfiler extends StorageAccessManager {

	public StorageAccessManagerProfiler(Profiler profiler) {
		super();
		this.profiler = profiler;
	}

	@Override
	public Set<Person> getAllPersons() {
		try {
			profiler.startMethod("StorageAccessManager.getAllPersons");
			return super.getAllPersons();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			profiler.stopMethod();
		}
		return null;
	}

	@Override
	public String getDataProperty(Node arg0, String arg1, Class<?> arg2) {
		try {
			profiler.startMethod("StorageAccessManager.getDataProperty");
			return super.getDataProperty(arg0, arg1, arg2);
		} finally {
			profiler.stopMethod();
		}
	}

	@Override
	public void setDataProperty(Node arg0, String arg1, Class<?> arg2,
			Object arg3) {
		try {
			profiler.startMethod("StorageAccessManager.setDataProperty");
			super.setDataProperty(arg0, arg1, arg2, arg3);
		} finally {
			profiler.stopMethod();
		}
	}

	@Override
	public Person getMe() throws KBException {
		try {
			profiler.startMethod("StorageAccessManager.getMe");
			return super.getMe();
		} finally {
			profiler.stopMethod();
		}
	}

	@Override
	public <K> Set<K> getObjectProperty(Node arg0, String arg1) {
		try {
			profiler.startMethod("StorageAccessManager.getObjectProperty");
			return super.getObjectProperty(arg0, arg1);
		} finally {
			profiler.stopMethod();
		}
	}

	@Override
	public <K> Set<K> getObjectProperty_inverse(Node arg0, String arg1) {
		try {
			profiler.startMethod("StorageAccessManager.getObjectProperty_inverse");
			return super.getObjectProperty_inverse(arg0, arg1);
		} finally {
			profiler.stopMethod();
		}
	}

	@Override
	public boolean setObjectProperty(Node arg0, String arg1, Object arg2) {
		try {
			profiler.startMethod("StorageAccessManager.setObjectProperty");
			return super.setObjectProperty(arg0, arg1, arg2);
		} finally {
			profiler.stopMethod();
		}
	}

	@Override
	public boolean deleteObjectProperty(Node arg0, String arg1, Object arg2) {
		try {
			profiler.startMethod("StorageAccessManager.deleteObjectProperty");
			return super.deleteObjectProperty(arg0, arg1, arg2);
		} finally {
			profiler.stopMethod();
		}
	}

	@Override
	public Person getPersonByUserId(String arg0) throws KBException {
		try {
			profiler.startMethod("StorageAccessManager.getPersonByUserId");
			return super.getPersonByUserId(arg0);
		} finally {
			profiler.stopMethod();
		}
	}

	@Override
	protected Node getPropertyNode(String propertyUri) {
		try {
			profiler.startMethod("StorageAccessManager.getPropertyNode");
			return super.getPropertyNode(propertyUri);
		} finally {
			profiler.stopMethod();
		}
	}

	private Profiler profiler;
}
