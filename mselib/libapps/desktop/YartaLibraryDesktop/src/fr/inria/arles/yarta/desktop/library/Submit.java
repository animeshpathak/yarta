package fr.inria.arles.yarta.desktop.library;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Submit {

	public static void submitString(String string) {
		String url = "http://arles.rocq.inria.fr/yarta/rdf/";
		String urlParameters = "rdf=" + string;

		try {
			doPost(url, urlParameters);
		} catch (Exception ex) {
			System.err.println("submitString: " + ex.toString());
		}
	}

	/**
	 * Does a No-Authentication post. User only when logging in.
	 * 
	 * @param url
	 * @param urlParameters
	 * @return
	 */
	public static String doPost(String url, String urlParameters)
			throws Exception {
		HttpURLConnection connection = (HttpURLConnection) new URL(url)
				.openConnection();
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setInstanceFollowRedirects(false);
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		connection.setRequestProperty("charset", "utf-8");
		connection.setRequestProperty("Content-Length",
				"" + Integer.toString(urlParameters.getBytes().length));
		connection.setUseCaches(false);

		DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();

		BufferedReader rd = new BufferedReader(new InputStreamReader(
				connection.getInputStream()));

		String response = "";
		String line = null;
		while ((line = rd.readLine()) != null) {
			response += line;
		}
		rd.close();
		connection.disconnect();
		return response;
	}
}
