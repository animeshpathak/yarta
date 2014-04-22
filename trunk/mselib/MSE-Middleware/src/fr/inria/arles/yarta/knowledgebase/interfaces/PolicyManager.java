package fr.inria.arles.yarta.knowledgebase.interfaces;

/**
 * The PolicyManager interface exposed to the programmer
 */
public interface PolicyManager {

	/**
	 * Returns the rules count;
	 * 
	 * @return int
	 */
	public int getRulesCount();

	/**
	 * Returns the rule at the specified position.
	 * 
	 * @param position
	 *            int
	 * @return String
	 */
	public String getRule(int position);

	/**
	 * Removes the rule at the specified position.
	 * 
	 * @param position
	 *            int
	 */
	public void removeRule(int position);

	/**
	 * Adds the specified rule.
	 * 
	 * @param rule
	 *            String
	 */
	public void addRule(String rule);
}
