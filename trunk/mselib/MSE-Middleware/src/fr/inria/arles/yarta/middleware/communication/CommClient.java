package fr.inria.arles.yarta.middleware.communication;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.kobjects.base64.Base64;

import fr.inria.arles.yarta.logging.YLoggerFactory;

/**
 * Class which contains static functions which will handle message passing
 * to/from the server.
 * 
 * Also since components are a bit hacky (GCM Intent Service, Connection,
 * YCommunicationManager) the message receives will be maintained / notified
 * here.
 */
public class CommClient {

	private static List<Receiver> callbacks = new ArrayList<Receiver>();

	private static void log(String format, Object... args) {
		YLoggerFactory.getLogger().d("CommClient", String.format(format, args));
	}

	public static final String ServerURL = "http://arles.rocq.inria.fr/yarta/gcm/";
	public static String userId;

	public static void addCallback(Receiver callback) {
		callbacks.add(callback);
	}

	public static void removeCallback(Receiver callback) {
		callbacks.remove(callback);
	}

	public static boolean init(String userId, String regId) {
		CommClient.userId = userId;
		String urlParams = String
				.format("add=1&uid=%s&regid=%s", userId, regId);
		try {
			String result = doPost(urlParams);
			log("init(%s, %s) returned %s", userId, regId, result);
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	public static boolean uninit(String userId, String regId) {
		try {
			String urlParams = String.format("del=1&uid=%s&regid=%s", userId,
					regId);
			String result = doPost(urlParams);
			log("uninit(%s, %s) returned %s", userId, regId, result);
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	public static int post(String userId, Message message) {
		byte[] data = YCommunicationManagerUtils.toBytes(message);
		String base64 = Base64.encode(data);
		
		String urlParams = String.format("push=1&from=%s&to=%s&message=%s",
				CommClient.userId, userId, base64);
		try {
			String result = doPost(urlParams);
			log("post(%s) returned %s", userId, result);
			if (result != null)
				return 0;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return -1;
	}

	public static Message get(String userId) {
		String urlParams = String.format("pop=1&to=%s", userId);
		Message message = null;

		try {
			String result = doPost(urlParams);
			log("get(%s) returned %s", userId, result);
			
			if (result.length() == 0)
				return null;

			JSONParser parser = new JSONParser();
			JSONObject object = (JSONObject) parser.parse(result);

			String from = (String) object.get("from");
			long messageid = (Long) object.get("messageid");

			result = "";

			URL oracle = new URL(ServerURL + "?read&messageid=" + messageid);
			HttpURLConnection con = (HttpURLConnection) oracle.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("content-type", "binary/data");

			con.setConnectTimeout(15 * 1000);
			con.setReadTimeout(15 * 1000);

			InputStream in = con.getInputStream();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] b = new byte[4096];
			int count;

			while ((count = in.read(b)) > 0) {
				baos.write(b, 0, count);
			}
			result = new String(baos.toByteArray());
			baos.close();
			byte[] data = Base64.decode(result.replace(" ", "+"));

			message = (Message) YCommunicationManagerUtils.toObject(data);
			if (message != null) {
				notifiyAllObservers(from, message);
			}
		} catch (Exception ex) {
			YLoggerFactory.getLogger().e("CommClient", "get " + ex);
		}
		return message;
	}
	
	private static void notifiyAllObservers(String from, Message message) {
		for (Receiver callback : callbacks) {
			callback.handleMessage(from, message);
		}
	}

	private static String doPost(String urlParameters) throws Exception {
		HttpURLConnection connection = (HttpURLConnection) new URL(ServerURL)
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
