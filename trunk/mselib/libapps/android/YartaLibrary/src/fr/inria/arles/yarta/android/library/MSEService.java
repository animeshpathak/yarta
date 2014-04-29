package fr.inria.arles.yarta.android.library;

/**
 * Used for communication between AidlService and the Android service. Its
 * responsibility is to initialize & uninitialize the mse objects.
 */
public interface MSEService {

	/**
	 * Initializes the pure MSE objects.
	 * 
	 * @param app
	 *            caller app
	 * @param source
	 *            source rdf file
	 * @param namespace
	 *            source rdf namespace
	 * @param policyFile
	 *            the policies file
	 * @return
	 */
	public boolean init(IMSEApplication app, String source, String namespace,
			String policyFile);

	/**
	 * Periodic save of KB.
	 * 
	 * @return
	 */
	public boolean save();

	/**
	 * Returns the user id, or null if no user is logged in.
	 * 
	 * @return String
	 */
	public String getUserId();

	/**
	 * Saves the current state or KB. If force is set to true, the objects are
	 * also uninitialized.
	 * 
	 * @return
	 */
	public boolean uninit(boolean force);

	public boolean clear();
}
