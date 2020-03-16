package blue.endless.wtrader;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.api.SyntaxError;

public class RestQuery {
	
	public static JsonElement start(String url) throws IOException {
		URL u = new URL(url);
		if (!u.getProtocol().equals("https")) throw new IOException("Only HTTPS connections are supported.");
		URLConnection conn = u.openConnection();
		if (conn instanceof HttpsURLConnection) {
			conn.connect();
			
			try {
				return Jankson.builder().build().loadElement(conn.getInputStream());
			} catch (SyntaxError e) {
				throw new IOException("The remote server did not reply with valid json.");
			}
		} else {
			throw new IOException("Epected HTTPS connection but got "+conn.getClass().getSimpleName());
		}
	}
}
