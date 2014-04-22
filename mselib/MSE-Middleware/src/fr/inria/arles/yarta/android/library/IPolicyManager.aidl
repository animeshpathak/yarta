package fr.inria.arles.yarta.android.library;

interface IPolicyManager {
	int getRulesCount();
	String getRule(int position);
	void removeRule(int position);
	void addRule(String rule);
}