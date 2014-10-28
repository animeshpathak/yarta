package fr.inria.arles.yarta.android.library;

interface IMSEApplication {

	void handleNotification(in String notification);

	boolean handleQuery(in String query);

	void handleKBReady(in String userId);
	
	void renameResource(in String oldURI, in String newURI);
	
	String getAppId();
}