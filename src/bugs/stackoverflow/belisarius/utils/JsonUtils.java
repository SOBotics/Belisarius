package bugs.stackoverflow.belisarius.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import bugs.stackoverflow.belisarius.services.ApiService;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.parser.Parser;

import java.io.IOException;

/**
 * Created by bhargav.h on 22-Jan-17.
 * AKA, TunaLib - All code is courtesy of Lord Tunaki
 */
public class JsonUtils {
	

	private long lastCall;
	private long throttle = 1L * 1000L;
	
    public synchronized JsonObject get(String url, String... data) throws IOException {
    	
    	long backOffUntil = ApiService.getBackOffUntil();
    	
    	if (backOffUntil > 0) {
			long timeToWait = backOffUntil - System.currentTimeMillis() + 1000L; 
			if (timeToWait > 0) {
				try {
					Thread.sleep(timeToWait);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			backOffUntil = 0L;
		}

		long curTime = System.currentTimeMillis();
		long timeToWait = throttle - (curTime - lastCall);
    	
		if (timeToWait > 0) {
			try {
				Thread.sleep(timeToWait);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		lastCall = System.currentTimeMillis();
    	
        Connection.Response response = Jsoup.connect(url).data(data).method(Connection.Method.GET).ignoreContentType(true).ignoreHttpErrors(true).execute();
        String json = response.body();
        if (response.statusCode() != 200) {
            throw new IOException("HTTP " + response.statusCode() + " fetching URL " + (url) + ". Body is: " + response.body());
        }
        JsonObject root = new JsonParser().parse(json).getAsJsonObject();
        return root;
    }

    public static String escapeHtmlEncoding(String message) {
        return Parser.unescapeEntities(JsonUtils.sanitizeChatMessage(message), false).trim();
    }
    public static String sanitizeChatMessage(String message) {
        return message.replaceAll("(\\[|\\]|_|\\*|`)", "\\\\$1");
    }
    


}
