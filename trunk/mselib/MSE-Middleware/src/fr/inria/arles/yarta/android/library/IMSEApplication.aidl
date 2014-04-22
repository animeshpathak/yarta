package fr.inria.arles.yarta.android.library;

interface IMSEApplication {

	void handleNotification(in String notification);

	boolean handleQuery(in String query);

	void handleKBReady(in String userId);
	
	String getAppId();
}