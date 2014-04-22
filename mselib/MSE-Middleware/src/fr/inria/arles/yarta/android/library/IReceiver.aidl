package fr.inria.arles.yarta.android.library;

interface IReceiver {

	String getAppId();

	void handleMessage(in String id, in Bundle message);

	Bundle handleRequest(in String id, in Bundle message);
}