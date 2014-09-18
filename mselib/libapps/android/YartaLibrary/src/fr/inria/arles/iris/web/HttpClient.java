package fr.inria.arles.iris.web;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.Locale;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import fr.inria.arles.yarta.android.library.util.Base64;

public class HttpClient {

	private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

	private String lastError = null;
	private String token = null;

	public void setToken(String token) {
		this.token = token;
	}

	public String getToken() {
		return token;
	}

	public String getLastError() {
		return lastError;
	}

	/**
	 * Does a No-Authentication post. User only when logging in.
	 * 
	 * @param url
	 * @param urlParameters
	 * @return
	 */
	public String doPost(String url, String urlParameters) throws Exception {
		HttpURLConnection connection = (HttpURLConnection) getURL(url)
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

	/**
	 * Does a POST request with authentication
	 * 
	 * @param url
	 * @param urlParameters
	 * @return
	 */
	public String doAuthenticatedPost(String url, String urlParameters)
			throws Exception {

		// add user authentication
		urlParameters += "&auth_token=" + token;
		HttpURLConnection connection = (HttpURLConnection) getURL(url)
				.openConnection();
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setInstanceFollowRedirects(false);
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		connection.setRequestProperty("Charset", "UTF-8");
		connection.setRequestProperty("Content-Length",
				"" + Integer.toString(urlParameters.getBytes().length));
		connection.setUseCaches(false);

		// API Authentication
		double ftime = System.currentTimeMillis();
		ftime /= 1000.0;
		String microtime = String.format(Locale.US, "%.4f", ftime);

		String postHash = calcMD5(urlParameters);

		String nonce = "randomstring";

		connection.setRequestProperty("X-Elgg-apikey", Strings.PublicKey);
		connection.setRequestProperty("X-Elgg-time", microtime);
		connection.setRequestProperty("X-Elgg-nonce", nonce);
		connection.setRequestProperty("X-Elgg-hmac-algo", "sha1");
		connection.setRequestProperty(
				"X-Elgg-hmac",
				calcHMAC(microtime, nonce, Strings.PublicKey,
						Strings.PrivateKey, "", postHash));

		connection.setRequestProperty("X-Elgg-posthash", postHash);
		connection.setRequestProperty("X-Elgg-posthash-algo", "md5");
		// API Authentication

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

	/**
	 * Does an Authenticated GET
	 * 
	 * @param url
	 * @param urlParameters
	 * @return
	 */
	public String doAuthenticatedGet(String url, String urlParameters)
			throws Exception {

		// add user authentication
		urlParameters += "&auth_token=" + token;
		HttpURLConnection connection = (HttpURLConnection) getURL(
				url + urlParameters).openConnection();
		connection.setDoOutput(false);
		connection.setDoInput(true);
		connection.setInstanceFollowRedirects(false);
		connection.setRequestMethod("GET");
		connection.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		connection.setRequestProperty("charset", "utf-8");
		connection.setRequestProperty("Content-Length", "0");
		connection.setUseCaches(false);

		// API Authentication
		double ftime = System.currentTimeMillis();
		ftime /= 1000.0;
		String microtime = String.format(Locale.US, "%.4f", ftime);

		String nonce = "randomstring";
		String hmac = calcHMAC(microtime, nonce, Strings.PublicKey,
				Strings.PrivateKey, urlParameters, "");

		connection.setRequestProperty("X-Elgg-apikey", Strings.PublicKey);
		connection.setRequestProperty("X-Elgg-time", microtime);
		connection.setRequestProperty("X-Elgg-nonce", nonce);
		connection.setRequestProperty("X-Elgg-hmac-algo", "sha1");
		connection.setRequestProperty("X-Elgg-hmac", hmac);

		// API Authentication

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

	/**
	 * Returns an URL with trailing question mark '?' if needed.
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	private URL getURL(String url) throws Exception {
		URL u = new URL(url);
		if (url.endsWith("?")) {
			Field f = u.getClass().getDeclaredField("file");
			f.setAccessible(true);

			String file = (String) f.get(u);

			if (!file.endsWith("?")) {
				f.set(u, file + "?");
			}
		}
		return u;
	}

	/**
	 * Calculates the HMAC value for this request.
	 * 
	 * @param microtime
	 * @param nonce
	 * @param publicKey
	 * @param privateKey
	 * @param params
	 * @param postHash
	 * @return
	 */
	private String calcHMAC(String microtime, String nonce, String publicKey,
			String privateKey, String params, String postHash) {
		try {
			SecretKeySpec signingKey = new SecretKeySpec(privateKey.getBytes(),
					HMAC_SHA1_ALGORITHM);

			Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
			mac.init(signingKey);
			mac.update(microtime.getBytes());
			mac.update(nonce.getBytes());
			mac.update(publicKey.getBytes());
			mac.update(params.getBytes());
			if (postHash.length() > 0) {
				mac.update(postHash.getBytes());
			}
			byte[] b = mac.doFinal();
			return URLEncoder.encode(Base64.encode(b), "UTF-8");
		} catch (Exception ex) {
			lastError = ex.toString();
		}
		return null;
	}

	/**
	 * Calculates the MD5
	 * 
	 * @param message
	 * @return
	 */
	private String calcMD5(String message) {
		try {
			MessageDigest m = MessageDigest.getInstance("MD5");
			m.reset();
			m.update(message.getBytes());
			byte[] digest = m.digest();
			BigInteger bigInt = new BigInteger(1, digest);
			String hashtext = bigInt.toString(16);
			// Now we need to zero pad it if you actually want the full 32
			// chars.
			while (hashtext.length() < 32) {
				hashtext = "0" + hashtext;
			}
			return hashtext;
		} catch (Exception ex) {
			lastError = ex.toString();
		}
		return null;
	}
}
