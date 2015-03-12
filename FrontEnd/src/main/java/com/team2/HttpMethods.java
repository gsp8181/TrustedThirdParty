package com.team2;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;




public class HttpMethods {
	/**
	 * Builds a URI object to the REST service from the variables provided
	 * 
	 * @param path
	 *            The path of the request for example /contracts/0 WITHOUT the
	 *            /rest part
	 * @param args
	 *            If using a query param for example login=true&things=this then
	 *            set kv pairs otherwise leave as null
	 * @return A URI object or null if there was an error
	 */
	public static URI buildUri(String path, Map<String, String> args) {
		return buildUri("ttp.gsp8181.co.uk", "/rest" + path, 443, true, args);
	}

	public static JSONObject sendgetjson(URI endpoint) throws Exception {
		CloseableHttpClient httpClient = HttpClients.createDefault();

		HttpGet req = new HttpGet(endpoint);
		CloseableHttpResponse response = httpClient.execute(req);
		String responseBody = EntityUtils.toString(response.getEntity());
		if (!response.getStatusLine().toString().startsWith("HTTP/1.1 2"))
			throw new Exception("Failed to GET : error "
					+ response.getStatusLine().toString());
		return new JSONObject(responseBody);
	}
	
	public static JSONArray sendgetjsonArray(URI endpoint) throws Exception {
		CloseableHttpClient httpClient = HttpClients.createDefault();

		HttpGet req = new HttpGet(endpoint);
		CloseableHttpResponse response = httpClient.execute(req);
		String responseBody = EntityUtils.toString(response.getEntity());
		if (!response.getStatusLine().toString().startsWith("HTTP/1.1 2"))
			throw new Exception("Failed to GET : error "
					+ response.getStatusLine().toString());
		return new JSONArray(responseBody); 
	}
	
	public static boolean senddeletejson(URI endpoint) throws Exception {
		CloseableHttpClient httpClient = HttpClients.createDefault();

		HttpDelete req = new HttpDelete(endpoint);
		CloseableHttpResponse response = httpClient.execute(req);
		if (!response.getStatusLine().toString().startsWith("HTTP/1.1 2"))
			throw new Exception("Failed to DELETE : error "
					+ response.getStatusLine().toString());
		return true;
	}

	public static JSONObject sendpostjson(URI endpoint, JSONObject message)
			throws Exception {
		CloseableHttpClient httpClient = HttpClients.createDefault();

		StringEntity params = new StringEntity(message.toString());
		HttpPost req = new HttpPost(endpoint);

		req.addHeader("Content-Type", "application/json");
		req.setEntity(params);
		CloseableHttpResponse response = httpClient.execute(req);
		String responseBody = EntityUtils.toString(response.getEntity());
		if (!response.getStatusLine().toString().startsWith("HTTP/1.1 2"))
			throw new Exception("Failed to POST : error "
					+ response.getStatusLine().toString() + ", " + responseBody);
		return new JSONObject(responseBody);
	}
	/**
	 * Builds a URI object from the variables provided
	 * 
	 * @param hostname
	 *            The hostname for example www.google.co.uk
	 * @param path
	 *            The path of the request for example /service/rest/contracts/0
	 * @param port
	 *            The port of the host
	 * @param secure
	 *            True to use https and false to use http
	 * @param args
	 *            If using a query param for example login=true&things=this then
	 *            set kv pairs otherwise leave as null
	 * @return A URI object or null if there was an error
	 */
	public static URI buildUri(String hostname, String path, int port,
			boolean secure, Map<String, String> args)  {
		URIBuilder uri = new URIBuilder();
		if (secure)
			uri.setScheme("https");
		else
			uri.setScheme("http");

		uri.setHost(hostname);
		uri.setPath(path);
		uri.setPort(port);
		if (args != null) {
			Iterator<Entry<String, String>> x = args.entrySet().iterator();
			while (x.hasNext()) {
				Entry<String, String> entry = x.next();
				uri.setParameter(entry.getKey(), entry.getValue());
			}
		}

		try {
			return uri.build();
		} catch (URISyntaxException e) {
			return null;
		}
	}
}
