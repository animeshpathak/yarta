package fr.inria.arles.yarta.desktop.library;

import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

public class RMIUtil {
	/**
	 * Start the RMI Registry handling all the exceptions.
	 */
	public static void startRegistry() {
		try {
			LocateRegistry.createRegistry(1099);
		} catch (Exception ex) {
		}
	}

	public static <K> K getObject(String name) {
		try {
			Remote object = Naming.lookup("/" + System.getenv("computername")
					+ "/" + name);
			return (K) object;
		} catch (Exception ex) {
		}
		return null;
	}

	public static void setObject(String name, Remote object) {
		try {
			if (object == null) {
				Naming.unbind("/" + System.getenv("computername") + "/" + name);
			} else {
				Naming.rebind("/" + System.getenv("computername") + "/" + name,
						object);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static <K> K exportObject(String name, Remote object) {
		try {
			Naming.rebind("/" + System.getenv("computername") + "/" + name,
					object);

			Remote o = Naming.lookup("/" + System.getenv("computername") + "/"
					+ name);
			return (K) o;
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}

	public static void unexportObject(String name, Remote object) {
		try {
			Naming.unbind("/" + System.getenv("computername") + "/" + name);
			UnicastRemoteObject.unexportObject(object, false);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
