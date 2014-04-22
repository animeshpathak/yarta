package fr.inria.arles.yarta.middleware.communication;

import java.io.Serializable;

import fr.inria.arles.yarta.knowledgebase.interfaces.Triple;

/**
 * The Message to say hello. Consists of the requestor's first name, last name,
 * and uniqueId
 */
public class BasicPersonInfo implements Serializable {

	/**
	 * the unique ID for serialization
	 */
	private static final long serialVersionUID = -4604337365330941461L;

	private String fName;
	private String lName;
	private String userId;
	private String email;

	private Triple userIdTriple;
	private Triple typeTriple;

	/**
	 * empty constructor, to be used when returning nothing
	 */
	public BasicPersonInfo() {
	}

	/**
	 * constructor, to set all fields.
	 * 
	 * @param fName
	 * @param lName
	 * @param userId
	 */
	public BasicPersonInfo(String fName, String lName, String userId,
			String email, Triple userIdTriple, Triple typeTriple) {
		this.fName = fName;
		this.lName = lName;
		this.userId = userId;
		this.email = email;
		this.userIdTriple = userIdTriple;
		this.typeTriple = typeTriple;
	}

	/**
	 * @return the fName
	 */
	public String getFName() {
		return fName;
	}

	/**
	 * @return the lName
	 */
	public String getLName() {
		return lName;
	}

	/**
	 * @return the uniqueId
	 */
	public String getUserId() {
		return userId;
	}
	
	/**
	 * @return the e-mail
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Gets the userid triple
	 * 
	 * @return Triple
	 */
	public Triple getUserIdTriple() {
		return userIdTriple;
	}

	/**
	 * Gets the type triple.
	 * 
	 * @return Triple
	 */
	public Triple getTypeTriple() {
		return typeTriple;
	}
}
