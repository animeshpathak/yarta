package fr.inria.arles.yarta.android.library;

/**
 * Used for communication between AidlService and the Android service. Its
 * responsibility is to initialize & uninitialize the mse objects.
 */
public interface MSEService {

	/**
	 * Initializes the pure MSE objects.
	 * @return
	 */
	public boolean init();

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
